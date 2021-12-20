package com.example.demo.src.review;

import com.example.demo.src.product.model.PatchProductReq;
import com.example.demo.src.review.model.*;
import com.example.demo.src.user.model.GetUserRes;
import com.example.demo.src.user.model.PostUserReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository //  [Persistence Layer에서 DAO를 명시하기 위해 사용]


public class ReviewDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    // 해당 userIdx를 갖는 유저조회
    public GetReviewRes getReview(int userIdx) {
        String getReviewQuery = "select * from Review where userIdx = ?"; // 해당 userIdx를 만족하는 유저를 조회하는 쿼리문
        int getReviewParams = userIdx;
        return this.jdbcTemplate.queryForObject(getReviewQuery,
                (rs, rowNum) -> new GetReviewRes(
                        rs.getInt("reviewIdx"),
                        rs.getInt("userIdx"),
                        rs.getInt("reviewer"),
                        rs.getString("comment")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getReviewParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }
    // User 테이블에 존재하는 전체 유저들의 정보 조회
    public List<GetReviewRes> getReviews() {
        String getReviewsQuery = "select * from Review"; //User 테이블에 존재하는 모든 회원들의 정보를 조회하는 쿼리
        return this.jdbcTemplate.query(getReviewsQuery,
                (rs, rowNum) -> new GetReviewRes(
                        rs.getInt("reviewIdx"),
                        rs.getInt("userIdx"),
                        rs.getInt("reviewer"),
                        rs.getString("comment")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        ); // 복수개의 회원정보들을 얻기 위해 jdbcTemplate 함수(Query, 객체 매핑 정보)의 결과 반환(동적쿼리가 아니므로 Parmas부분이 없음)
    }

    // 해당 nickname을 갖는 유저들의 정보 조회
    public List<GetReviewRes> getReviewsByUserIdx(int userIdx) {
        String getReviewsByUserIdxQuery = "select * from Review where userIdx =?"; // 해당 닉네임을 만족하는 유저를 조회하는 쿼리문
        int getReviewsByUserIdxParams = userIdx;
        return this.jdbcTemplate.query(getReviewsByUserIdxQuery,
                (rs, rowNum) -> new GetReviewRes(
                        rs.getInt("reviewIdx"),
                        rs.getInt("userIdx"),
                        rs.getInt("reviewer"),
                        rs.getString("comment")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getReviewsByUserIdxParams); // 해당 닉네임을 갖는 모든 User 정보를 얻기 위해 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // 리뷰 작성
    public int createReview(PostReviewReq postReviewReq) {
        String createReviewQuery = "insert into Review (userIdx,reviewer, comment) VALUES (?,?,?)"; // 실행될 동적 쿼리문
        Object[] createReviewParams = new Object[]{postReviewReq.getUserIdx(),postReviewReq.getReviewer(), postReviewReq.getComment()}; // 동적 쿼리의 ?부분에 주입될 값
        this.jdbcTemplate.update(createReviewQuery, createReviewParams);
        // email -> postUserReq.getEmail(), password -> postUserReq.getPassword(), nickname -> postUserReq.getNickname() 로 매핑(대응)시킨다음 쿼리문을 실행한다.
        // 즉 DB의 User Table에 (email, password, nickname)값을 가지는 유저 데이터를 삽입(생성)한다.

        String lastInserIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 userIdx번호를 반환한다.
    }
    // 리뷰 comment 변경
    public int modifyComment(PatchReviewReq patchReviewReq) {
        String modifyCommentQuery = "update Review set comment = ? where reviewIdx = ? "; // 해당 userIdx를 만족하는 User를 해당 nickname으로 변경한다.
        Object[] modifyCommentParams = new Object[]{patchReviewReq.getComment(),patchReviewReq.getReviewIdx() }; // 주입될 값들(nickname, userIdx) 순

        return this.jdbcTemplate.update(modifyCommentQuery, modifyCommentParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }

}
