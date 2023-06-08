package com.example.demoo.controllers;

import com.example.demoo.domain.DefClient;
import com.example.demoo.domain.DefUser;
import com.example.demoo.domain.HttpResponse;
import com.example.demoo.domain.UserPrinciple;
import com.example.demoo.exceptions.ExceptionHandling;
import com.example.demoo.exceptions.domain.EmailExistException;
import com.example.demoo.exceptions.domain.EmailNotFoundException;
import com.example.demoo.exceptions.domain.UserNotFoundException;
import com.example.demoo.exceptions.domain.UsernameExistException;
import com.example.demoo.repositories.DefClientRepository;
import com.example.demoo.repositories.DefUserRepository;
import com.example.demoo.services.DefClientService;
import com.example.demoo.services.DefUserService;
import com.example.demoo.utility.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.example.demoo.constants.SecurityConstant.JWT_TOKEN_HEADER;


@RestController
@RequestMapping(path = {"/", "/user"})
public class DefUserController extends ExceptionHandling {

    public static final String EMAIL_SENT = "An email with a new password is sent to : ";
    public static final String USER_DELETED = "User has been deleted successfully";
    private DefUserService defUserService;
    private DefClientService defClientService;
    private DefUserRepository defUserRepository;

    private DefClientRepository defClientRepository;
    private AuthenticationManager authenticationManager;
    private JWTTokenProvider jwtTokenProvider;

    @Autowired
    public DefUserController(DefUserService defUserService,
                             DefClientService defClientService,
                             DefUserRepository defUserRepository,
                             DefClientRepository defClientRepository,
                             AuthenticationManager authenticationManager,
                             JWTTokenProvider jwtTokenProvider) {
        this.defUserService = defUserService;
        this.defClientService = defClientService;
        this.defUserRepository = defUserRepository;
        this.defClientRepository = defClientRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/home")
    public String showUser()throws UserNotFoundException {
        throw new UserNotFoundException("User was not found");
    }

    @PostMapping("/register")
    public ResponseEntity<DefUser> register(@RequestBody DefUser user)
            throws UserNotFoundException, EmailExistException, UsernameExistException, MessagingException {
        DefUser newUser = defUserService.register(user.getUsername(), user.getEmail());
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody DefUser user, HttpServletResponse response) {
        authenticate(user.getUsername(), user.getPassword());
        DefUser loginUser = defUserService.findDefUserByUsername(user.getUsername());
        UserPrinciple userPrinciple = new UserPrinciple(loginUser);
        HttpHeaders jwtHeader = getJWTHeader(userPrinciple);

        if (loginUser.getDefClient() == null || isProfileIncomplete(loginUser.getDefClient())) {
            response.setHeader("Location", "/complete-your-profile");
            return new ResponseEntity<>(HttpStatus.FOUND);
        } else {
            return new ResponseEntity<>(loginUser, jwtHeader, HttpStatus.OK);
        }
    }

    private boolean isProfileIncomplete(DefClient client) {
        return client.getFirstName() == null || client.getLastName() == null || client.getMobile() == null ||
                client.getBirthDate() == null;
    }

    private ResponseEntity<DefUser> firstLoginCheck(DefUser user) {
        DefClient client = defClientService.findDefClientByUser(user);
        if (client == null){
            return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
        }
        else {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
    }

    @GetMapping("/first-login")
    public ResponseEntity<DefClient> firstLogin() {
        DefUser user = defUserService.getLoggedInUser();
        DefClient client = defClientService.findDefClientByUser(user);
        if (client == null) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(client, HttpStatus.OK);
        }
    }

    @GetMapping("/complete-your-profile")
    public String showProfileForm(Model model) throws EmailNotFoundException {
        DefUser user = getCurrentUser();
        DefClient client = defClientRepository.findDefClientByUser(user);
        if (client == null) {
            client = new DefClient();
            client.setUser(user);
            if (user != null) {
                client.setEmail(user.getEmail());
            }
            else {
                throw new EmailNotFoundException("No Email associated with this username");
            }
        }
        model.addAttribute("Client", client);
        return "profile-form";
    }

    private DefUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String username = userDetails.getUsername();
            return defUserRepository.findDefUserByUsername(username);
        }
        return null;
    }

    @PostMapping("/add")
    public ResponseEntity<DefUser> addNewUser(@RequestParam("username") String username,
                                              @RequestParam("email") String email,
                                              @RequestParam("role") String role,
                                              @RequestParam("isActive") String isActive,
                                              @RequestParam("NotLocked") String isNotLocked)
            throws EmailExistException, UsernameExistException {
        DefUser newUser = defUserService.addNewUser(username, email, role, Boolean.parseBoolean(isActive),
                Boolean.parseBoolean(isNotLocked));
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<DefUser> updateUser(@RequestParam("currentUser") String currentUser,
                                              @RequestParam("username") String username,
                                              @RequestParam("email") String email,
                                              @RequestParam("role") String role,
                                              @RequestParam("isActive") String isActive,
                                              @RequestParam("NotLocked") String isNotLocked)
            throws EmailExistException, UsernameExistException {
        DefUser updatedUser = defUserService.updateUser(currentUser, username, email, role,
                Boolean.parseBoolean(isActive),
                Boolean.parseBoolean(isNotLocked));
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<DefUser> getUser(@PathVariable("username") String username) {
        DefUser user = defUserService.findDefUserByUsername(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @GetMapping("/list")
    public ResponseEntity<List<DefUser>> getAllUsers() {
        List<DefUser> users = defUserService.getUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/resetpassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email)
            throws EmailNotFoundException, MessagingException {
        defUserService.resetPassword(email);
        return response(HttpStatus.OK, EMAIL_SENT + email);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("id") Long id) {
        defUserService.deleteUser(id);
        return response(HttpStatus.NO_CONTENT, USER_DELETED);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        HttpResponse body = new HttpResponse(httpStatus.value(),
                httpStatus,
                httpStatus.getReasonPhrase().toUpperCase(),
                message.toUpperCase());
        return new ResponseEntity<>(body, httpStatus);
    }

    private HttpHeaders getJWTHeader(UserPrinciple user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJWTToken(user));
        return headers;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}