package com.tenco.blog.reply;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// @Repository - 부모클래스에 정의되어 있음
public interface ReplyRepository extends JpaRepository<Reply, Integer> {
    // 1. 등록 및 수정 save(Board entity)
    // 2. 단건 조회 : findById(Integer id)
    // 3. 전체 조회 : findAll()
    // 4. 삭제 : deleteById(Integer id) - reply
    // 5. 데이터 개수: count()
    // 6. 존재 여부 확인: existsById(Integer id)

    // 기본적인 CRUD 자동 완성 및 추가 편의 기능 자동 생성
    // 게시글 ID로 댓글 목록 조회(한번에 댓글 작성자 정보 포함 - JOIN FETCH 사용)
//    select r.*, b.*, u.* from reply_tb r
//    inner join board_tb b on r.board_id = b.id
//    inner join user_tb u on r.user_id = u.id
//    where r.board_id = 1
//    order by r.created_at asc;
    // JPQL 문법으로 변환
    @Query("""
                    select r from Reply r
                                join fetch r.user
                                join fetch r.board
                                where r.board.id = :boardId Order by r.createdAt asc
            """)
    List<Reply> findByBoardIdWithUser(@Param("boardId") Integer boardId);

    // 즉 board_id로 댓글을 삭제하는 기능이 없다.

    /**
     * 이전 수정, 삭제 기능에서 수정은 더티 체킹으로 처리했고
     * 삭제는 기본적으로 제공하는 em.remove() 메소드를 사용해서 처리했다.
     * 지금은 직접 JPQL 쿼리를 선언해서 delete 처리하는 구문이라 다른 상황이다.
     * @Query(...) << JPA 기본적으로 select 쿼리로만 인식하기 떄문에
     * insert, update, delete는 JPA에게 select쿼리가 아님을 알려줘야 제대로 동작
     * 그 어노테이션이 @Modifying이다.
     * @param boardId
     */
    @Modifying
    @Query("delete from Reply r where r.board.id = :boardId")
    void deleteByBoardId(@Param("boardId") Integer boardId);
}
