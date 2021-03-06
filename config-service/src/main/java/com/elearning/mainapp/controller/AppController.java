package com.elearning.mainapp.controller;

import com.elearning.mainapp.models.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppController {

    public static boolean loged = false;
    @RequestMapping("/")
    public String viewHomePage(Model model) {
        if(!loged){
            return "redirect:/login";
        }
        ModuleList moduleList = new ModuleList();
        try {
            RestTemplate restTemplate = new RestTemplate();
            moduleList = restTemplate.getForObject("http://localhost:8081/", ModuleList.class);
        }catch(Exception e){
            e.printStackTrace();
        }
        model.addAttribute("moduleList", moduleList.getModuleList());
        return "index";
    }
    @RequestMapping("/new")
    public String showNewProductPage(Model model) {
        if(!loged){
            return "redirect:/login";
        }
        Modules module = new Modules();
        model.addAttribute("module", module);
        return "new_product";
    }
    @RequestMapping("/login")
    public String logout(Model model) {
        Login login = new Login();
        model.addAttribute("login", login);
        return "login";
    }
    @RequestMapping("/logout")
    public String login(Model model) {
        loged = false;
        return "redirect:/login";
    }
    @RequestMapping("/log")
    public String log(@ModelAttribute("Login") Login login) {
        TeacherList teacherList = new TeacherList();
        try {
            RestTemplate restTemplate = new RestTemplate();
            teacherList = restTemplate.getForObject("http://localhost:8082/",TeacherList.class);
            for(Teacher t : teacherList.getTeachersList()){
                if(t.getEmail().equals(login.getEmail())){
                    if(t.getPassword().equals(login.getPassword())){
                        loged = true;
                        return "redirect:/";
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return "redirect:/login";
    }
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveProduct(@ModelAttribute("Modules") Modules modules) {
        if(!loged){
            return "redirect:/login";
        }
        RestTemplate restTemplate = new RestTemplate();
        String baseUrl = "http://localhost:8081/save/"+modules.getId()+"/"+modules.getCredit()+"/"+modules.getCoefficient()+"/"+modules.getCourshours()+"/"+modules.getTdhours()+"/"+modules.getTphours()+"/"+modules.getName()+"/"+modules.getLastname()+"/"+modules.getEmail()+"";
        try {
            System.out.println(modules);
            restTemplate.getForObject(baseUrl, Modules.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }
    @RequestMapping("/edit/{id}")
    public ModelAndView showEditProductPage(@PathVariable(name = "id") int id) {
        ModelAndView mav = new ModelAndView("edit_product");
        //Product product = service.get(id);
        RestTemplate restTemplate = new RestTemplate();
        Modules module = restTemplate.getForObject("http://localhost:8081/"+id,Modules.class);
        mav.addObject("module", module);
        return mav;
    }
    @RequestMapping("/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") int id) {
        if(!loged){
            return "redirect:/login";
        }
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject("http://localhost:8081/delete/"+id,String.class);
        return "redirect:/";
    }

}
