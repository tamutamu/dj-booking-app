package pnodder.controllers;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import pnodder.converters.ArtistFormatter;
import pnodder.converters.BookingFormatter;
import pnodder.model.Booking;
import pnodder.services.ArtistService;
import pnodder.services.BookingService;

import javax.validation.Valid;

@Controller
public class MainController {

    private ArtistService artistService;
    private BookingService bookingService;

    public MainController(ArtistService artistService, BookingService bookingService) {
        this.artistService = artistService;
        this.bookingService = bookingService;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addCustomFormatter(new ArtistFormatter(artistService));
        binder.addCustomFormatter(new BookingFormatter(bookingService));
    }
    
    @ModelAttribute
    public void initialiseModel(Model model) {
        model.addAttribute("allArtists", artistService.findAll());
        model.addAttribute("booking", new Booking());
    }

    @GetMapping("/booking")
    public String getBookingForm(Model model) {
        Subject currentUser = SecurityUtils.getSubject();
        model.addAttribute("isLoggedIn", currentUser.isAuthenticated() ? true : false);
        model.addAttribute("isAdminUser", currentUser.hasRole("admin") ? true : false);
        return "booking";
    }

    @PostMapping("/submitbooking")
    public String submitBooking(@Valid Booking booking, BindingResult result, Model model) {
        if (!result.hasErrors()) {
            bookingService.save(booking);
            return "redirect:/booking";
        } else {
            return "booking";
        }
    }
    
    @GetMapping("/mybookings")
    public String getMyBookings(Model model) {
        model.addAttribute("allBookings", bookingService.findAllDistinct());
        return "bookings";
    }

    @PostMapping("/editbooking")
    public String editBooking(@RequestParam("bookingName") String name, Model model) {
        model.addAttribute("bookingedit", bookingService.findBookingByName(name));
        return "editbooking";
    }

    @PostMapping("/deletebooking")
    public String deleteBooking(@RequestParam("bookingName") String name) {
        bookingService.deleteByName(name);
        return "redirect:/mybookings";
    }
    
    @PostMapping("/updatebooking")
    public String updateBooking(Booking booking,
                              @RequestParam("bookingName") String name,
                              BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            bookingService.updateByName(name, booking);
            return "redirect:/mybookings";
        } else {
            return "editbooking";
        }
    }

}
