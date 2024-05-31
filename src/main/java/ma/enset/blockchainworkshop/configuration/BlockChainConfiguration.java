package ma.enset.blockchainworkshop.configuration;

import ma.enset.blockchainworkshop.entity.BlockChain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
@Configuration
public class BlockChainConfiguration {

    @Bean
    @Scope("singleton")
    public BlockChain blockchain() {
        return new BlockChain(2);
    }
}

