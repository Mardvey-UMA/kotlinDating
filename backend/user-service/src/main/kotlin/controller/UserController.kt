package controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController {
    @GetMapping()
    fun getUser(@RequestHeader(value = "X-User-Name", required = false) username: String?){
        println(username)
    }
}