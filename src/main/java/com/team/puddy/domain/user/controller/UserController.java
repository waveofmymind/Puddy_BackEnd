package com.team.puddy.domain.user.controller;



import com.team.puddy.domain.article.dto.response.ResponseArticleExcludeCommentDto;
import com.team.puddy.domain.question.dto.response.ResponseQuestionExcludeAnswerDto;
import com.team.puddy.domain.user.dto.request.*;
import com.team.puddy.domain.user.dto.response.ResponsePostDto;
import com.team.puddy.domain.user.dto.response.ResponseUserInfoDto;
import com.team.puddy.domain.user.dto.response.TokenReissueDto;

import com.team.puddy.domain.user.service.UserService;

import com.team.puddy.global.common.dto.Response;
import com.team.puddy.global.config.auth.JwtUserDetails;
import com.team.puddy.global.config.security.jwt.LoginToken;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/users")
@CrossOrigin("http://localhost:3000")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public Response<LoginToken> login(@RequestBody @Valid LoginUserRequest request) {
        LoginToken token = userService.login(request);
        return Response.success(token);
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("/join")
    public Response<Void> join(@RequestBody @Valid RegisterUserRequest request) {
        userService.join(request);
        return Response.success();
    }

    @PostMapping("/login/reissue")
    public LoginToken tokenReissue(@RequestBody TokenReissueDto tokenReissueDto) {
        log.info("access: {}", tokenReissueDto.accessToken());
        log.info("refresh: {}", tokenReissueDto.refreshToken());
        return userService.reissueToken(tokenReissueDto);
    }

    @DeleteMapping(value = "/logout", produces = "application/json")
    public void logout(@AuthenticationPrincipal JwtUserDetails userDetails) {
        userService.logout(userDetails.getUserId());
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PatchMapping("/update-auth")
    public void updateAuth(@AuthenticationPrincipal JwtUserDetails user) {
        userService.updateAuth(user.getUserId());
    }

    @GetMapping("/posts")
    public Response<?> myPost(@RequestParam("page") int page,@RequestParam(value = "type",defaultValue = "question") String type,
                                @AuthenticationPrincipal JwtUserDetails user) {
        Pageable pageable = PageRequest.of(page - 1, 10);
        ResponsePostDto myPost = userService.getMyPost(user.getUserId(), type, pageable);
        return Response.success(myPost);
    }


    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping("/me")
    public Response<?> me(@AuthenticationPrincipal JwtUserDetails user) {
        ResponseUserInfoDto userInfo = userService.getUserInfo(user.getUserId());
        return Response.success(userInfo);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/update-profile")
    public Response<?> updateProfile(@RequestParam(value = "images", required = false) MultipartFile file,
                                     @RequestPart("request") UpdateNicknameDto requestDto,
                                     @AuthenticationPrincipal JwtUserDetails user) throws IOException {

        userService.updateProfile(user.getUserId(), requestDto.nickname(), file);
        return Response.success();
    }

    @PostMapping("/find-account")
    public Response<String> findAccount(@RequestBody FindAccountDto accountDto) {
        String findAccount = userService.findAccount(accountDto);
        return Response.success(findAccount);
    }

    @PatchMapping("/change-password")
    public Response<Void> changePassword(@RequestBody ChangePasswordDto passwordDto,
                                         @AuthenticationPrincipal JwtUserDetails user) {
        userService.changePassword(passwordDto.password(),user.getUserId());
        return Response.success();

    }



    @PostMapping("/duplicate-email")
    @Operation(summary = "이메일 중복 검사 메서드")
    public void duplicateEmail(@RequestBody DuplicateEmailRequest request) {

        userService.duplicateEmailCheck(request.email());
        log.error("중복된 이메일입니다.");
    }

    @PostMapping("/duplicate-account")
    @Operation(summary = "아이디 중복 검사 메서드")
    public void duplicateAccount(@RequestBody DuplicateAccountRequest request) {

        userService.duplicateAccountCheck(request.account());
    }


    @PostMapping("/duplicate-nickname")
    @Operation(summary = "닉네임 중복 검사 메서드")
    public void duplicateNickname(@RequestBody DuplicateNicknameRequest request) {
        userService.duplicateNicknameCheck(request.nickname());
    }


}
