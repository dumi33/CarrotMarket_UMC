package com.example.demo.src.heart;



import com.example.demo.src.product.model.GetProductRes;
import com.example.demo.src.product.model.PatchProductReq;
import com.example.demo.src.product.model.Product;
import com.example.demo.src.user.model.PostUserReq;
import com.example.demo.src.user.model.PostUserRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.heart.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController // Rest API 또는 WebAPI를 개발하기 위한 어노테이션. @Controller + @ResponseBody 를 합친것.
// @Controller      [Presentation Layer에서 Contoller를 명시하기 위해 사용]
//  [Presentation Layer?] 클라이언트와 최초로 만나는 곳으로 데이터 입출력이 발생하는 곳
//  Web MVC 코드에 사용되는 어노테이션. @RequestMapping 어노테이션을 해당 어노테이션 밑에서만 사용할 수 있다.
// @ResponseBody    모든 method의 return object를 적절한 형태로 변환 후, HTTP Response Body에 담아 반환.
@RequestMapping("/app/hearts")

public class HeartController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private final HeartProvider heartProvider;
    @Autowired
    private final HeartService heartService;
    @Autowired
    private final JwtService jwtService;

    public HeartController(HeartProvider heartProvider, HeartService heartService, JwtService jwtService) {
        this.heartProvider = heartProvider;
        this.heartService = heartService;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }
    /**
     * 모든 관심들  조회 API
     * [GET] /hearts
     *
     * 또는
     *
     * 해당 productIdx을 갖는 제품들의 정보 조회 API
     * [GET] /hearts? productIdx=
     */

    //Query String
    @ResponseBody   // return되는 자바 객체를 JSON으로 바꿔서 HTTP body에 담는 어노테이션.
    //  JSON은 HTTP 통신 시, 데이터를 주고받을 때 많이 쓰이는 데이터 포맷.
    @GetMapping("") // (GET) 127.0.0.1:9000/app/hearts
    // GET 방식의 요청을 매핑하기 위한 어노테이션
    public BaseResponse<List<GetHeartRes>> getHearts(@RequestParam(required = false, defaultValue = "-1") int productIdx) {
        //  @RequestParam은, 1개의 HTTP Request 파라미터를 받을 수 있는 어노테이션(?뒤의 값). default로 RequestParam은 반드시 값이 존재해야 하도록 설정되어 있지만, (전송 안되면 400 Error 유발)
        //  지금 예시와 같이 required 설정으로 필수 값에서 제외 시킬 수 있음
        //  defaultValue를 통해, 기본값(파라미터가 없는 경우, 해당 파라미터의 기본값 설정)을 지정할 수 있음
        try {
            if (productIdx == -1) { // query string인 productName이 없을 경우, 그냥 전체 제품정보를 불러온다.
                List<GetHeartRes> getHeartsRes = heartProvider.getHearts();
                return new BaseResponse<>(getHeartsRes);
            }
            // query string인 nickname이 있을 경우, 조건을 만족하는 유저정보들을 불러온다.
            List<GetHeartRes> getHeartsRes = heartProvider.getHeartsByProductIdx(productIdx);
            return new BaseResponse<>(getHeartsRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 하트 삭제 API
     * [DELETE] /app/hearts/:heartIdx
     */
    @ResponseBody
    @DeleteMapping("/{heartIdx}")
    // Path-variable
    public BaseResponse<String> deleteHearts(@PathVariable("heartIdx") int heartIdx, @RequestBody Heart heart) {
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
            DeleteHeartReq deleteHeartReq = new DeleteHeartReq(heartIdx);
            heartService.deleteHeart(deleteHeartReq);

            String result = "heart가 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * Heart 추가 API
     * [POST] /hearts
     */
    // Body
    @ResponseBody
    @PostMapping("/add")    // POST 방식의 요청을 매핑하기 위한 어노테이션
    public BaseResponse<PostHeartRes> createHeart(@RequestBody PostHeartReq postHeartReq) {

        try {
            PostHeartRes postHeartRes = heartService.createHeart(postHeartReq);
            return new BaseResponse<>(postHeartRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
