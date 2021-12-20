package com.example.demo.src.review;


import com.example.demo.src.product.model.PatchProductReq;
import com.example.demo.src.product.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.review.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;
@RestController
@RequestMapping("/app/reviews")

public class ReviewController {
    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log를 남기기: 일단은 모르고 넘어가셔도 무방합니다.

    @Autowired  // 객체 생성을 스프링에서 자동으로 생성해주는 역할. 주입하려 하는 객체의 타입이 일치하는 객체를 자동으로 주입한다.
    private final ReviewProvider reviewProvider;
    @Autowired
    private final ReviewService reviewService;
    @Autowired
    private final JwtService jwtService;

    public ReviewController(ReviewProvider reviewProvider, ReviewService reviewService, JwtService jwtService) {
        this.reviewProvider = reviewProvider;
        this.reviewService = reviewService;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }

    /**
     * 모든 리뷰  조회 API
     * [GET] /reviews
     *
     * 또는
     *
     * 해당 유저에 대한 리뷰 조회 API
     * [GET] /reviews? userIdx=
     */
    //Query String
    @ResponseBody   // return되는 자바 객체를 JSON으로 바꿔서 HTTP body에 담는 어노테이션.
    //  JSON은 HTTP 통신 시, 데이터를 주고받을 때 많이 쓰이는 데이터 포맷.
    @GetMapping("") // (GET) 127.0.0.1:9000/app/users
    // GET 방식의 요청을 매핑하기 위한 어노테이션
    public BaseResponse<List<GetReviewRes>> getReviews(@RequestParam(required = false, defaultValue = "-1") int userIdx) {
        //  @RequestParam은, 1개의 HTTP Request 파라미터를 받을 수 있는 어노테이션(?뒤의 값). default로 RequestParam은 반드시 값이 존재해야 하도록 설정되어 있지만, (전송 안되면 400 Error 유발)
        //  지금 예시와 같이 required 설정으로 필수 값에서 제외 시킬 수 있음
        //  defaultValue를 통해, 기본값(파라미터가 없는 경우, 해당 파라미터의 기본값 설정)을 지정할 수 있음
        try {

            if (userIdx == -1) { // query string인 userIdx이 없을 경우, 그냥 전체 리뷰정보를 불러온다.
                List<GetReviewRes> getReviewsRes = reviewProvider.getReviews();
                return new BaseResponse<>(getReviewsRes);
            }
            // query string인 nickname이 있을 경우, 조건을 만족하는 유저정보들을 불러온다.
            List<GetReviewRes> getReviewsRes = reviewProvider.getReviewsByUserIdx(userIdx);
            return new BaseResponse<>(getReviewsRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 리뷰 작성 API
     * [POST] /reviews
     */
    // Body
    @ResponseBody
    @PostMapping("")    // POST 방식의 요청을 매핑하기 위한 어노테이션
    public BaseResponse<PostReviewRes> createReview(@RequestBody PostReviewReq postReviewReq) {
        //  @RequestBody란, 클라이언트가 전송하는 HTTP Request Body(우리는 JSON으로 통신하니, 이 경우 body는 JSON)를 자바 객체로 매핑시켜주는 어노테이션
        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
        // email에 값이 존재하는지, 빈 값으로 요청하지는 않았는지 검사합니다. 빈값으로 요청했다면 에러 메시지를 보냅니다.
        try {
            PostReviewRes postReviewRes = reviewService.createReview(postReviewReq);
            return new BaseResponse<>(postReviewRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 회원 1명의 리뷰 조회 API
     * [GET] /reviews/:userIdx
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{userIdx}") // (GET) 127.0.0.1:9000/app/users/:userIdx
    public BaseResponse<GetReviewRes> getReview(@PathVariable("userIdx") int userIdx) {
        // @PathVariable RESTful(URL)에서 명시된 파라미터({})를 받는 어노테이션, 이 경우 userId값을 받아옴.
        //  null값 or 공백값이 들어가는 경우는 적용하지 말 것
        //  .(dot)이 포함된 경우, .을 포함한 그 뒤가 잘려서 들어감
        // Get Users
        try {
            GetReviewRes getReviewRes = reviewProvider.getReview(userIdx);
            return new BaseResponse<>(getReviewRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }
    /**
     * 유저정보변경 API
     * [PATCH] /app/reviews/:reviewIdx
     */
    @ResponseBody
    @PatchMapping("/{reviewIdx}")
    // Path-variable
    public BaseResponse<String> modifyComment(@PathVariable("reviewIdx") int reviewIdx, @RequestBody Review review) {
        try {
/**
 *********** 해당 부분은 7주차 - JWT 수업 후 주석해체 해주세요!  ****************
 //jwt에서 idx 추출.
 int userIdxByJwt = jwtService.getUserIdx();
 //userIdx와 접근한 유저가 같은지 확인
 if(userIdx != userIdxByJwt){
 return new BaseResponse<>(INVALID_USER_JWT);
 }
 //같다면 유저네임 변경
 **************************************************************************
 */
            PatchReviewReq patchReviewReq = new PatchReviewReq(reviewIdx, review.getComment());
            reviewService.modifyComment(patchReviewReq);

            String result = "리뷰 comment가 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
