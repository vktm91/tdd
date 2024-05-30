package com.example.demo.user.controller;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.mock.TestContainer;
import com.example.demo.user.controller.response.MyProfileResponse;
import com.example.demo.user.controller.response.UserResponse;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class UserControllerTest {

    // 현재 이 테스트에서는 get메서드만 필요한데 다른 메서드들이 너무 많아서 인터페이스 분리 하여
    // userService가 아닌 userReadService만 작성해주면 되도록 한다.

    @Test
    void 사용자는_특정_유저의_정보를_개인정보는_소거된채_전달_받을_수_있다() {
        // 1과 같이 stub을 하는 코드로 작성 할 수 있으나,
        // 그렇게 되면 하위 클래스에 어떤 메서드가 호출되면 이런 응답을 내려저줘야 한다는것 자체가 구현을 강제하는 것이다.
        // 책임을 위임하고 구현은 알아서 하라는 것과 멀어지기 때문에 추천하지 않는다.
        // 대신 2와 같이 TestContainer를 만들어서 스프링 IOC 컨테이너를 흉내내는 코드를 작성해보도록 한다.

        //// 1. stub
//        // given
//        UserController userController = UserController.builder()
//                .userReadService(new UserReadService() {
//                    @Override
//                    public User getByEmail(String email) {
//                        return null;
//                    }
//
//                    @Override
//                    public User getById(long id) {
//                        return User.builder()
//                                .id(1L)
//                                .email("kok202@naver.com")
//                                .nickname("kok202")
//                                .address("Seoul")
//                                .status(UserStatus.ACTIVE)
//                                .certificationCode("aaaa-aaaa-aaaa-aaaa")
//                                .build();
//                    }
//                })
//                .build();
//        // when
//        ResponseEntity<UserResponse> result = userController.getUserById(1);
//
//        // then
//        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
//        assertThat(result.getBody()).isNotNull();
//        assertThat(result.getBody().getId()).isEqualTo(1);
//        assertThat(result.getBody().getEmail()).isEqualTo("kok202@naver.com");
//        assertThat(result.getBody().getNickname()).isEqualTo("kok202");
//        assertThat(result.getBody().getStatus()).isEqualTo(UserStatus.ACTIVE);

        //// 2. TestContainer
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();

        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("kok202@naver.com")
                .nickname("kok202")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaa-aaaa-aaaa-aaaa")
                .lastLoginAt(100L)
                .build());

        // when
        ResponseEntity<UserResponse> result = testContainer.userController
                .getUserById(1);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo(1);
        assertThat(result.getBody().getEmail()).isEqualTo("kok202@naver.com");
        assertThat(result.getBody().getNickname()).isEqualTo("kok202");
        assertThat(result.getBody().getLastLoginAt()).isEqualTo(100);
        assertThat(result.getBody().getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void 사용자는_존재하지_않는_유저의_아이디로_api_호출할_경우_404_응답을_받는다() {
    //// 1. stub

//        // given
//        UserController userController = UserController.builder()
//                .userReadService(new UserReadService() {
//                    @Override
//                    public User getByEmail(String email) {
//                        return null;
//                    }
//
//                    @Override
//                    public User getById(long id) {
//                        throw new ResourceNotFoundException("Users", id);
//                    }
//                })
//                .build();
//
//        // when
//        // then
//        assertThatThrownBy(() -> {
//            userController.getUserById(123456789);
//        }).isInstanceOf(ResourceNotFoundException.class);

        //// TestContainer
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();

        // when
        // then
        assertThatThrownBy(() -> {
           testContainer.userController.getUserById(1);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void 사용자는_인증_코드로_계정을_활성화_시킬_수_있다() {
        // given
        TestContainer testContainer = TestContainer.builder()
                        .build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("kok202@naver.com")
                .nickname("kok202")
                .address("Seoul")
                .status(UserStatus.PENDING)
                .certificationCode("aaaa-aaaa-aaaa-aaaa")
                .lastLoginAt(100L)
                .build());

        // when
        ResponseEntity<Void> result = testContainer.userController.verifyEmail(1, "aaaa-aaaa-aaaa-aaaa");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(302));
        assertThat(testContainer.userRepository.getById(1).getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void 사용자는_인증_코드가_일치하지_않을_경우_권한_없음_에러를_내려준다() {
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("kok202@naver.com")
                .nickname("kok202")
                .address("Seoul")
                .status(UserStatus.PENDING)
                .certificationCode("aaaa-aaaa-aaaa-aaaa")
                .lastLoginAt(100L)
                .build());

        // when
        assertThatThrownBy(() -> {
            testContainer.userController.verifyEmail(1, "aaaa-aaaa-bbbb-bbbb");
        }).isInstanceOf(CertificationCodeNotMatchedException.class);
    }

    @Test
    void 사용자는_내_정보를_불러올_때_개인정보인_주소도_갖고_올_우_있다() {
        // given
        TestContainer testContainer = TestContainer.builder()
                .clockHolder(() -> 1678530673958L)
//                .clockHolder(new ClockHolder() {
//                    @Override
//                    public long millis() {
//                        return 1678530673958L;
//                    }
//                })
                .build();

        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("kok202@naver.com")
                .nickname("kok202")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaa-aaaa-aaaa-aaaa")
                .lastLoginAt(100L)
                .build());

        // when
        ResponseEntity<MyProfileResponse> result = testContainer.userController.getMyInfo("kok202@naver.com");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo(1);
        assertThat(result.getBody().getEmail()).isEqualTo("kok202@naver.com");
        assertThat(result.getBody().getNickname()).isEqualTo("kok202");
        assertThat(result.getBody().getLastLoginAt()).isEqualTo(1678530673958L);
        assertThat(result.getBody().getAddress()).isEqualTo("Seoul");
        assertThat(result.getBody().getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void 사용자는_내_정보를_수정할_수_있다() {
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();

        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("kok202@naver.com")
                .nickname("kok202")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaa-aaaa-aaaa-aaaa")
                .lastLoginAt(100L)
                .build());

        // when
        ResponseEntity<MyProfileResponse> result = testContainer.userController.updateMyInfo("kok202@naver.com", UserUpdate.builder()
                .nickname("뿌꾸미")
                .address("햇빛드는곳")
                .build());

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo(1);
        assertThat(result.getBody().getEmail()).isEqualTo("kok202@naver.com");
        assertThat(result.getBody().getNickname()).isEqualTo("뿌꾸미");
        assertThat(result.getBody().getLastLoginAt()).isEqualTo(100);
        assertThat(result.getBody().getAddress()).isEqualTo("햇빛드는곳");
        assertThat(result.getBody().getStatus()).isEqualTo(UserStatus.ACTIVE);
    }
}
