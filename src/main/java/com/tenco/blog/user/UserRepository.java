package com.tenco.blog.user;

// SRP - 단일 책임의 원칙

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

// IoC 메모리에 객체를 띄우는 역할
@Repository
@RequiredArgsConstructor
public class UserRepository {
    //    @Autowired // DI - 스프링 프레임 워크가 주소값 자동 주입
    // @RequiredArgsConstructor 쓰면 AutoWired 쓸 필요없다
    private final EntityManager em;

    // 회원 가입 요청시 --> insert
    @Transactional
    public User save(User user) {
        // 매개 변수로 들어온 user Object는 비영속 상태

        em.persist(user); // 영속상태
        // user 영속 상태
        // 리턴시 user 오브젝트는 영속화 된 상태이다.
        return user;
    }

    // 사용자 이름 중복확인
    public User findByUsername(String username) {
        String jpqlStr = """
                select u from User u where u.username = :username
                """;

//        Query query = em.createQuery(jpqlStr, User.class);
//        query.setParameter("username", username);
//        User userEntity = (User) query.getSingleResult();
        try {
            return em.createQuery(jpqlStr, User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    // 로그인 요청시 --> select

    public User findByUsernameAndPassword(String username, String password) {

        String jpqlStr = """
                select u from User u where u.username = :username and u.password = :password
                """;

        try {
            return em.createQuery(jpqlStr, User.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
