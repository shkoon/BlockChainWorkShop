package ma.enset.blockchainworkshop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.enset.blockchainworkshop.entity.Block;
import ma.enset.blockchainworkshop.entity.BlockChain;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class P2P extends TextWebSocketHandler {
    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
    private final BlockChain blockchain;



    public P2P(BlockChain blockchain) {
        this.blockchain = blockchain;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(blockchain.getChain())));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Block[] receivedBlocks = mapper.readValue(message.getPayload(), Block[].class);
        synchronizeBlockchain(receivedBlocks);
    }

    private void synchronizeBlockchain(Block[] receivedBlocks) {
        Block latestReceivedBlock = receivedBlocks[receivedBlocks.length - 1];
        Block latestLocalBlock = blockchain.getLatestBlock();

        if (latestReceivedBlock.getIndex() > latestLocalBlock.getIndex()) {
            if (latestLocalBlock.getCurrentHash().equals(latestReceivedBlock.getPreviousHash())) {
                blockchain.addBlock(latestReceivedBlock);
                broadcastNewBlock(latestReceivedBlock);
            } else if (receivedBlocks.length == 1) {
                broadcastNewBlock(latestLocalBlock);
            } else {
                blockchain.replaceChain(receivedBlocks);
                broadcastNewBlock(latestReceivedBlock);
            }
        }
    }

    public void broadcastNewBlock(Block block) {
        sessions.forEach(session -> {
            try {
                session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(block)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
