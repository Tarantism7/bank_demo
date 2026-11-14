
package com.example.bankdemo.controller;

import com.example.bankdemo.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
public class UserTransferController {

    private final TransferService transferService;

    @PostMapping("/user/transfer-to-user")
    public String transferToUser(
            @RequestParam Long fromCardId,
            @RequestParam Long toCardId,
            @RequestParam String recipientUsername,
            @RequestParam BigDecimal amount,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {

        try {

            if (principal == null) {
                throw new IllegalArgumentException(
                        "Please sign in before transferring money."
                );
            }

            transferService.transferToUser(
                    principal.getName(),
                    recipientUsername,
                    fromCardId,
                    toCardId,
                    amount
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Money successfully sent to " +
                            recipientUsername + "!"
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Transfer failed. Please try again."
            );
        }

        return "redirect:/user/dashboard";
    }
}
