package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

//정말 중요!!!!
@Slf4j
@SpringBootTest
public class InternalCallV1Test {

    @Autowired
    CallService callService;

    @Test
    void printProxy() {
        log.info("callService class={}", callService.getClass());
    }

    @Test
    void internalCall() {
        callService.internal();
    }

    /**
     * 프록시를 거치지 않고 실제 객체 내부 internal()을 호출해서 트랜잭션이 적용 X
     *
     * -동작흐름
     * 테스트 callService.external() 호출 -> 프록시 transaction 없어서 적용 X 실제 객체 (target.external() 호출)
     * -> 실제 객체 external 수행 중 internal() 호출 -> internal() 수행 후 반환 -> 프록시로 돌아와서 종료
     * internal()이 프록시 거치지 않음 그래서 트랜잭션 적용 X
     */
    @Test
    void externalCall() {
        callService.external();
    }

    @TestConfiguration
    static class InternalCallV1TestConfig {
        @Bean
        CallService callService() {
            return new CallService();
        }
    }


    @Slf4j
    static class CallService{

        public void external() {
            log.info("call external");
            printTxInfo();
            internal();
        }

        @Transactional
        public void internal() {
            log.info("call internal");
            printTxInfo();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
        }
    }
}
