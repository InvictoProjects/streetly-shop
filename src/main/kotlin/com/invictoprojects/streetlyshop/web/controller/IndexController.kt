package com.invictoprojects.streetlyshop.web.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import springfox.documentation.annotations.ApiIgnore
import javax.servlet.http.HttpServletResponse

@ApiIgnore
@Controller
class IndexController {

    @GetMapping("/")
    fun redirect(response: HttpServletResponse) = response.sendRedirect("/swagger-ui/")
}
