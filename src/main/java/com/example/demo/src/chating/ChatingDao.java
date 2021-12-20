package com.example.demo.src.chating;


import com.example.demo.src.chating.model.*;
import com.example.demo.src.product.model.GetProductRes;
import com.example.demo.src.product.model.PostProductReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository //  [Persistence Layer에서 DAO를 명시하기 위해 사용]

/**
 * DAO란?
 * 데이터베이스 관련 작업을 전담하는 클래스
 * 데이터베이스에 연결하여, 입력 , 수정, 삭제, 조회 등의 작업을 수행
 */


public class ChatingDao {
    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************

    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    // ******************************************************************************
    // Product 테이블에 존재하는 전체 제품들의 정보 조회
    // 채팅등록
    public int createChating(PostChatingReq postChatingReq) {
        String createChatingQuery = "insert into Chating (roomIdx, sender, content) VALUES (?,?,?)"; // 실행될 동적 쿼리문
        Object[] createChatingParams = new Object[]{postChatingReq.getRoomIdx(),postChatingReq.getSender(), postChatingReq.getContent()}; // 동적 쿼리의 ?부분에 주입될 값
        this.jdbcTemplate.update(createChatingQuery, createChatingParams);
        // email -> postUserReq.getEmail(), password -> postUserReq.getPassword(), nickname -> postUserReq.getNickname() 로 매핑(대응)시킨다음 쿼리문을 실행한다.
        // 즉 DB의 User Table에 (email, password, nickname)값을 가지는 유저 데이터를 삽입(생성)한다.

        String lastInserIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 userIdx번호를 반환한다.
    }
    // Product 테이블에 존재하는 전체 제품들의 정보 조회
    public List<GetChatingRes> getChatings() {
        String getChatingsQuery = "select * from Chating"; //User 테이블에 존재하는 모든 회원들의 정보를 조회하는 쿼리
        return this.jdbcTemplate.query(getChatingsQuery,
                (rs, rowNum) -> new GetChatingRes(
                        rs.getInt("chatIdx"),
                        rs.getInt("roomIdx"),
                        rs.getInt("sender"),
                        rs.getString("content")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        ); // 복수개의 회원정보들을 얻기 위해 jdbcTemplate 함수(Query, 객체 매핑 정보)의 결과 반환(동적쿼리가 아니므로 Parmas부분이 없음)
    }


    // 해당 ProductName을 갖는 유저들의 정보 조회
    public List<GetChatingRes> getChatingsByRoomIdx(int roomIdx) {
        String getChaingsByRoomIdxQuery = "select * from Chating where roomIdx =?"; // 해당 닉네임을 만족하는 유저를 조회하는 쿼리문
        int getChaingsByRoomIdxParams = roomIdx;
        return this.jdbcTemplate.query(getChaingsByRoomIdxQuery,
                (rs, rowNum) -> new GetChatingRes(
                        rs.getInt("chatIdx"),
                        rs.getInt("roomIdx"),
                        rs.getInt("sender"),
                        rs.getString("content")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getChaingsByRoomIdxParams); // 해당 닉네임을 갖는 모든 User 정보를 얻기 위해 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }
}
