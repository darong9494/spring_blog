package com.tenco.blog.board;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // IoC + DI
@RequiredArgsConstructor
public class BoardNativeRepository {
    // EntityManager: JPA 핵심 인터페이스
    // 데이터베이스와 모든 작업을 담당
    private final EntityManager em;

    //트랜잭션 처리해줌
    @Transactional
    public void save(String title, String content, String username) {
        Query query = em.createNativeQuery("insert into board_tb(title,content,username, created_at) values (?,?,?, now())");
        query.setParameter(1, title);
        query.setParameter(2, content);
        query.setParameter(3, username);
        query.executeUpdate();
    }

    // 게시글 목록 조회 메소드
    public List<Board> findAll() {
        String sql = """
                select * from board_tb order by id desc
                """;
        // while(rs.next) (Board board = new Board(); board.setTitle(rs.getString("title")))
        // rs.getString("title") >> new Board() board.title = "rs.getString("title")"
        Query query = em.createNativeQuery(sql, Board.class);

        // while(rs.next) ~~~~ 쓸 필요 없다.
        return query.getResultList();
    }

    // 게시글 상세 보기 (특정 ID로 조회)
    public Board findById(Integer id) {

        String strQuery = """
                select * from board_tb where id = ?
                """;

        try {
            Query query = em.createNativeQuery(strQuery, Board.class);
            query.setParameter(1, id);
            return (Board) query.getSingleResult();
        } catch (RuntimeException e) {
            return null;
        }
    }

    // 특정 게시글 삭제 요청
    @Transactional
    public void deleteById(Integer id) {
        String sql = """
                delete from board_tb where id = ?
        """;

        Query query = em.createNativeQuery(sql, Board.class);
        query.setParameter(1, id);

        query.executeUpdate();
    }

    // 게시글 수정하기
    @Transactional
    public boolean updateById(String username, String title, String content, Integer id) {
        String queryStr = """
                update board_tb set username = ?, title = ?, content = ? where id = ?
                """;

        Query query = em.createNativeQuery(queryStr);
        query.setParameter(1, username);
        query.setParameter(2, title);
        query.setParameter(3, content);
        query.setParameter(4, id);
        int rows = query.executeUpdate();
        if (rows > 0) {
            return  true;
        } else  {
            return false;
        }
    }
}
