package vn.edu.hcmute.uteshop;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.AccessDeniedException;
import vn.edu.hcmute.uteshop.dto.*;
import vn.edu.hcmute.uteshop.repository.*;
import vn.edu.hcmute.uteshop.service.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
class FullWorkflowTest {
 @Autowired AuthService auth;
 @Autowired UserRepository users;
 @Autowired UserService userService;
 @Autowired ProductService products;
 @Autowired PasswordEncoder encoder;
 @Test void registrationOtpAndPasswordReset() {
  String name="test"+UUID.randomUUID().toString().substring(0,8);
  RegisterDTO form=new RegisterDTO();form.setUsername(name);form.setEmail(name+"@example.com");
  form.setFullName("Người dùng thử");form.setPassword("Test@12345");form.setConfirmPassword("Test@12345");
  String code=auth.register(form);
  assertFalse(users.findByEmailIgnoreCase(form.getEmail()).orElseThrow().isEnabled());
  assertThrows(IllegalArgumentException.class,()->auth.resendRegister(form.getEmail()));
  assertFalse(auth.verifyRegister(form.getEmail(),"invalid"));
  assertTrue(auth.verifyRegister(form.getEmail(),code));
  assertFalse(auth.verifyRegister(form.getEmail(),code));
  assertTrue(users.findByEmailIgnoreCase(form.getEmail()).orElseThrow().isEnabled());
  assertThrows(IllegalArgumentException.class,()->auth.register(form));
  String reset=auth.forgotPassword(form.getEmail());
  assertFalse(auth.resetPassword(form.getEmail(),"invalid","NewPass@123"));
  assertTrue(auth.resetPassword(form.getEmail(),reset,"NewPass@123"));
  assertFalse(auth.resetPassword(form.getEmail(),reset,"Again@12345"));
  assertTrue(encoder.matches("NewPass@123",users.findByEmailIgnoreCase(form.getEmail()).orElseThrow().getPassword()));
 }
 @Test void userAndProductCrudOwnershipSearchAndPagination() {
  var admin=users.findByEmailIgnoreCase("admin@uteshop.vn").orElseThrow();
  var student=users.findByEmailIgnoreCase("user@uteshop.vn").orElseThrow();
  String name="crud"+UUID.randomUUID().toString().substring(0,8);
  UserForm form=new UserForm();form.setUsername(name);form.setEmail(name+"@example.com");form.setFullName("CRUD test");form.setPassword("Test@12345");
  userService.create(form);
  var owner=users.findByEmailIgnoreCase(form.getEmail()).orElseThrow();
  form.setFullName("Đã sửa");userService.update(owner.getId(),form,admin.getId());
  assertEquals("Đã sửa",userService.findForm(owner.getId()).getFullName());
  assertEquals(1,userService.search(name,0).getTotalElements());
  ProductDTO dto=new ProductDTO();dto.setName(name);dto.setDescription("unique-"+name);dto.setPrice(new BigDecimal("1000"));
  dto.setId(99999L);products.create(dto,null,owner.getId());
  var page=products.search("unique-"+name,0,owner.getId(),false);
  assertEquals(1,page.getTotalElements());assertEquals(6,page.getSize());
  Long id=page.getContent().get(0).getId();assertNotEquals(99999L,id);
  assertEquals(0,products.search(name,0,student.getId(),false).getTotalElements());
  assertThrows(AccessDeniedException.class,()->products.findForEditing(id,student.getId(),false));
  assertThrows(AccessDeniedException.class,()->products.delete(id,student.getId(),false));
  dto.setPrice(new BigDecimal("2000"));products.update(id,dto,null,owner.getId(),false);
  assertEquals(0,new BigDecimal("2000").compareTo(products.findForEditing(id,owner.getId(),false).getPrice()));
  assertEquals(1,products.countFor(owner.getId()));
  products.delete(id,owner.getId(),false);assertEquals(0,products.countFor(owner.getId()));
  userService.delete(owner.getId(),admin.getId());assertFalse(users.existsById(owner.getId()));
  assertThrows(IllegalArgumentException.class,()->userService.delete(admin.getId(),admin.getId()));
 }
}
