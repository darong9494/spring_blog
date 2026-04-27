package com.tenco.blog.board;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}
