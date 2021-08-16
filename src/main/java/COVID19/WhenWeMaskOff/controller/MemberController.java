package COVID19.WhenWeMaskOff.controller;

import COVID19.WhenWeMaskOff.domain.Member;
import COVID19.WhenWeMaskOff.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MemberController {
    private final MemberService memberService;
    @Autowired
    public MemberController(MemberService memberService){
        this.memberService = memberService;
    }

    @GetMapping("/")
    public String home(){
        return "home";
    }
    @GetMapping("members/new")
    public String singIn(){
        return "members/signin";
    }
    @PostMapping("members/new")
    public String join(Member form){
        memberService.join(form);
        return "redirect:/";
    }

}
