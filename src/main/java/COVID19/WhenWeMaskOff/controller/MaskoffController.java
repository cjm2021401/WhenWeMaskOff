package COVID19.WhenWeMaskOff.controller;

import COVID19.WhenWeMaskOff.domain.ApiData;
import COVID19.WhenWeMaskOff.domain.Member;
import COVID19.WhenWeMaskOff.service.MaskoffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class MaskoffController {
    private final MaskoffService maskoffService;
    @Autowired
    public MaskoffController(MaskoffService maskoffService){
        this.maskoffService=maskoffService;
    }

    @GetMapping("maskoff/search")
    public String search(){
        return "maskoff/search";
    }

    @PostMapping("maskoff/search")
    public String checkCorona(Member form, Model model) {
        ApiData result=maskoffService.getApiData(form.getId());

        model.addAttribute("percent", 70);
        System.out.println(result.getTotalSecondCnt());
        return "maskoff/result";
    }


}
