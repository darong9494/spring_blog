package com.tenco.blog.board;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // IoC
@RequiredArgsConstructor // DI 처리됨
public class BoardPersistRepository {
    // JPA 핵심 인터페이스
    // 영속성 컨텍스트를 관리하고 엔티티의 생명주기를 제어하는 역할
    // @Autowired // DI
    private final EntityManager em; // final을 사용하면 성능 개선이 조금 된다.

    // 의존주입 (외부에서 생성되어있는 객체의 주소값을 주입받다.)
//    public BoardPersistRepository(EntityManager em) {
//        this.em = em;
//    }

    // 게시글 저장
    @Transactional
    public Board save(Board board) {
        // 1. 매개변수로 받은 board는 비영속상태
        //  -- 아직 영속성 컨텍스트에 관리되고 있지 않은 상태
        //  -- 아직 데이터베이스와 연관없는 순수 JAVA 객체일뿐이다.

        // em.createNativeQuery("insert into... board_tb...); 쓸필요없다.
        em.persist(board); // insert 처리 완료

        // 2. board객체를 영속성 컨텍스트에 넣어둔다. (SQL저장소에 넣어둠)
        //  -- 영속성 컨텍스트에 들어가더라도 아직 DB에 실제 insert한 상태는 아니다.

        // 3. 트랜잭션 >> 커밋시점에 실제 DB에 접근해서 insert 구문이 수행된다.

        // 4. board객체의 id 변수값을 1차 캐시에 map구조로 보관되어진다.
        // 1차 캐시에 들어간 영속상태로 변경된 Object를 리턴한다.
        return board;
    }

    // JPQL을 사용한 게시글 목록 조회
    public List<Board> findAll() {
        // JPQL : 엔티티 객체를 대상으로하는 객체지향 쿼리
        // Board는 엔티티 클래스명, b는 별칭으로 사용
        // 주의! 테이블명이 아닌 클래스명 사용해야함(엔티티명)
        String jpql = """
                select b from Board b order by b.createdAt desc
                """;

        List<Board> boardList = em.createQuery(jpql, Board.class).getResultList();
        return boardList;
    }

    // 게시글 상세보기 요청 (조회) (필수값 기본키로 조회)
    public Board findById(Integer id) {
        // 영속성 컨텍스트를 사용하기 위해서
        // 1차 캐시공간에 저장된거 바로 읽어내서 쓸라고 데이터베이스까지 가지말고
        // 1. 엔티티 매니저에서 제공하는 메소드를 활용하는 방법
        Board board = em.find(Board.class, id);
        // 2. JPQL 문법으로 Board를 조회하는 방법
        String jpql = """
                select b from Board b where b.id = :id
                """;

        return em.createQuery(jpql, Board.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    // 게시글 삭제
    @Transactional
    public void deleteById(Integer id) {
        // 1. 먼저 삭제하고자하는 엔티티를 조회해야함
        // 1.1 조회가 되었기 때문에 board는 영속화된 상태가 되었다.
        Board board = em.find(Board.class, id);

        if (board == null) {
            throw new IllegalArgumentException("삭제할 게시글을 찾을 수 없습니다: " + id);
        }

        em.remove(board);
    }
}
