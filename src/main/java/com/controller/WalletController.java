package com.controller;

import io.swagger.annotations.*;
import com.manager.UserManager;
import com.manager.WalletManager;
import com.model.Users;
import com.requestdto.RechargeWalletRequest;
import com.responsedto.RechargeWalletResponse;
import com.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/wallet")
@Api(tags = "wallet")
public class WalletController {

    @Autowired
    private WalletManager walletManager;
    @Autowired
    private UserManager userManager;

    @PutMapping("/recharge")
    @ApiOperation(value = "${WalletController.recharge}",response = RechargeWalletResponse.class)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 406, message = "Recharge Failed"),
            @ApiResponse(code = 401, message = "Expired or invalid JWT token")})
    public ResponseEntity<?> walletRecharge(@ApiParam("Create Admin") @RequestBody RechargeWalletRequest request, @RequestHeader("Authorization") String jwt) {
        try {
            Users user = userManager.getUserByToken(jwt);
            RechargeWalletResponse rechargeWalletResponse = walletManager.rechargeWallet(request,user);
            return new ResponseEntity<>(rechargeWalletResponse, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return CommonUtils.getResponseEntity("Recharge Failed", "", Arrays.asList(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
