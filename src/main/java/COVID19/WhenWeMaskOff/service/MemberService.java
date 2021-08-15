package COVID19.WhenWeMaskOff.service;

import COVID19.WhenWeMaskOff.domain.Member;
import COVID19.WhenWeMaskOff.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository){
        this.memberRepository=memberRepository;
    }

    /**
     *회원가입
     */
    public String join(Member member){
        memberRepository.save(member);
        return member.getId();
    }
    /**
     * 중복검사
     */
    public void check(Member member){
        memberRepository.findById(member.getId()).ifPresent(m->{
            throw new IllegalStateException("이미 존재하는 회원입니다");
        });
    }
}
