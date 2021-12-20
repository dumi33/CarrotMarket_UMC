package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.review.model.*;
import com.example.demo.src.user.UserDao;
import com.example.demo.src.user.model.GetUserRes;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import static com.example.demo.config.BaseResponseStatus.*;

@Service

public class ReviewProvider {

    private final ReviewDao reviewDao;
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired //readme 참고
    public ReviewProvider(ReviewDao reviewDao, JwtService jwtService) {
        this.reviewDao = reviewDao;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }

    // 해당 userIdx를 갖는 User의 정보 조회
    public GetReviewRes getReview(int userIdx) throws BaseException {
        try {
            GetReviewRes getReviewRes = reviewDao.getReview(userIdx);
            return getReviewRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // User들의 정보를 조회
    public List<GetReviewRes> getReviews() throws BaseException {
        try {
            List<GetReviewRes> getReviewRes = reviewDao.getReviews();
            return getReviewRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 해당 nickname을 갖는 User들의 정보 조회
    public List<GetReviewRes> getReviewsByUserIdx(int userIdx) throws BaseException {
        try {
            List<GetReviewRes> getReviewRes = reviewDao.getReviewsByUserIdx(userIdx);
            return getReviewRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
