package ma.enset.blockchainworkshop.controller;

import ma.enset.blockchainworkshop.entity.Transaction;
import ma.enset.blockchainworkshop.entity.Wallet;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/wallet")
public class WalletController {
    private Wallet wallet;

    @PostMapping("/create")
    public ResponseEntity<String> createWallet() {
        try {
            wallet = new Wallet();
            return ResponseEntity.ok("Wallet created. Address: " + wallet.getAddress());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to create wallet.");
        }
    }

    @PostMapping("/transaction")
    public ResponseEntity<String> createTransaction(@RequestParam String recipient, @RequestParam double amount) {
        try {
            Transaction transaction = wallet.createTransaction(recipient, amount);
            return ResponseEntity.ok("Transaction created: " + transaction);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to create transaction.");
        }
    }

    @GetMapping("/address")
    public ResponseEntity<String> getAddress() {
        if (wallet != null) {
            return ResponseEntity.ok("Wallet address: " + wallet.getAddress());
        } else {
            return ResponseEntity.status(404).body("Wallet not found.");
        }
    }

}
