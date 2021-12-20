package com.example.demo.src.product;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.product.model.*;
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
@RequestMapping("/app/products")
public class ProductController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private final ProductProvider productProvider;
    @Autowired
    private final ProductService productService;
    @Autowired
    private final JwtService jwtService;

    public ProductController(ProductProvider productProvider, ProductService productService, JwtService jwtService) {
        this.productProvider = productProvider;
        this.productService = productService;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }

    // ******************************************************************************
    /**
     * 상품등록 API
     * [POST] /products
     */
    // Body
    @ResponseBody
    @PostMapping("")    // POST 방식의 요청을 매핑하기 위한 어노테이션
    public BaseResponse<PostProductRes> createProduct(@RequestBody PostProductReq postProductReq) {
        //  @RequestBody란, 클라이언트가 전송하는 HTTP Request Body(우리는 JSON으로 통신하니, 이 경우 body는 JSON)를 자바 객체로 매핑시켜주는 어노테이션

        //  detail에 값이 존재하는지, 빈 값으로 요청하지는 않았는지 검사합니다. 빈값으로 요청했다면 에러 메시지를 보냅니다.
        if (postProductReq.getProductDetail() == null) {
            return new BaseResponse<>(POST_PRODUCT_EMPTY_DETAIL);
        }
        try {
            PostProductRes postProductRes = productService.createProduct(postProductReq);
            return new BaseResponse<>(postProductRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 모든 제품들  조회 API
     * [GET] /products
     *
     * 또는
     *
     * 해당 productIdx을 갖는 제품들의 정보 조회 API
     * [GET] /products? productIdx=
     */

    //Query String
    @ResponseBody   // return되는 자바 객체를 JSON으로 바꿔서 HTTP body에 담는 어노테이션.
    //  JSON은 HTTP 통신 시, 데이터를 주고받을 때 많이 쓰이는 데이터 포맷.
    @GetMapping("") // (GET) 127.0.0.1:9000/app/products
    // GET 방식의 요청을 매핑하기 위한 어노테이션
    public BaseResponse<List<GetProductRes>> getProducts(@RequestParam(required = false) String productName) {
        //  @RequestParam은, 1개의 HTTP Request 파라미터를 받을 수 있는 어노테이션(?뒤의 값). default로 RequestParam은 반드시 값이 존재해야 하도록 설정되어 있지만, (전송 안되면 400 Error 유발)
        //  지금 예시와 같이 required 설정으로 필수 값에서 제외 시킬 수 있음
        //  defaultValue를 통해, 기본값(파라미터가 없는 경우, 해당 파라미터의 기본값 설정)을 지정할 수 있음
        try {
            if (productName == null) { // query string인 productName이 없을 경우, 그냥 전체 제품정보를 불러온다.
                List<GetProductRes> getProductsRes = productProvider.getProducts();
                return new BaseResponse<>(getProductsRes);
            }
            // query string인 nickname이 있을 경우, 조건을 만족하는 유저정보들을 불러온다.
            List<GetProductRes> getProductsRes = productProvider.getProductsByProductName(productName);
            return new BaseResponse<>(getProductsRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저정보변경 API
     * [PATCH] /app/products/:productIdx
     */
    @ResponseBody
    @PatchMapping("/{productIdx}")
    // Path-variable
    public BaseResponse<String> modifyProductName(@PathVariable("productIdx") int productIdx, @RequestBody Product product) {
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
            PatchProductReq patchProductReq = new PatchProductReq(productIdx, product.getProductName());
            productService.modifyProductName(patchProductReq);

            String result = "상품정보가 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}



