package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.service.BookService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService service;

    // default uploads folder relative to working directory (read from properties)
    @Value("${app.upload.dir:uploads/images}")
    private String uploadDir;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public String listBooks(@RequestParam(value = "q", required = false) String q, Model model) {
        model.addAttribute("books", (q == null || q.isEmpty()) ? service.getAll() : service.search(q));
        model.addAttribute("q", q);
        return "books";
    }

    @GetMapping("/add")
    public String showAddForm(HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("userRole"))) return "redirect:/login";
        model.addAttribute("book", new Book());
        return "add-book";
    }

    @PostMapping(value = "/add")
    public String addBook(@ModelAttribute("book") @Valid Book book,
                          BindingResult br,
                          @RequestParam(name = "imageFile", required = false) MultipartFile imageFile,
                          HttpSession session,
                          Model model) {

        // Role check
        if (!"ADMIN".equals(session.getAttribute("userRole"))) {
            return "redirect:/login";
        }

        // validation errors from @Valid
        if (br.hasErrors()) {
            br.getAllErrors().forEach(e -> System.out.println("Binding error: " + e));
            model.addAttribute("errors", br.getAllErrors());
            return "add-book";
        }

        // If a file was uploaded, save it and set imageUrl to /images/<filename>
        if (imageFile != null && !imageFile.isEmpty()) {
            // Basic content-type check (only accept image/*)
            String contentType = Optional.ofNullable(imageFile.getContentType()).orElse("");
            if (!contentType.toLowerCase().startsWith("image/")) {
                model.addAttribute("error", "Uploaded file must be an image.");
                return "add-book";
            }

            try {
                Path imagesDir = Paths.get(uploadDir).toAbsolutePath().normalize();
                if (!Files.exists(imagesDir)) Files.createDirectories(imagesDir);

                // Use a safe unique filename: uuid + original extension (if available)
                String original = imageFile.getOriginalFilename();
                String ext = "";
                if (original != null && original.contains(".")) {
                    ext = original.substring(original.lastIndexOf('.'));
                }
                String filename = UUID.randomUUID().toString() + ext;
                Path target = imagesDir.resolve(filename);

                // Copy file to external folder (overwrite if exists)
                Files.copy(imageFile.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

                // Save the public path used by Thymeleaf: /images/<filename>
                book.setImageUrl("/images/" + filename);

                // Helpful debug log
                System.out.println("BookController: saved image -> " + target.toAbsolutePath());

            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("error", "Failed to upload image. Try again.");
                return "add-book";
            }

        } else {
            // No file uploaded — check if the form supplied an external image URL and validate it
            String external = book.getImageUrl();
            if (external != null && !external.isBlank()) {
                external = external.trim();
                // Basic URL validation: allow only http/https
                try {
                    URL u = new URL(external);
                    String p = u.getProtocol();
                    if (!"http".equalsIgnoreCase(p) && !"https".equalsIgnoreCase(p)) {
                        model.addAttribute("error", "Image URL must start with http or https.");
                        return "add-book";
                    }
                    // keep external URL as-is (Thymeleaf will render it)
                    book.setImageUrl(external);
                } catch (Exception ex) {
                    model.addAttribute("error", "Invalid image URL.");
                    return "add-book";
                }
            }
            // if neither file nor external url provided, imageUrl remains null (optional)
        }

        // Save to DB
        service.addBook(book);
        return "redirect:/books";
    }
}