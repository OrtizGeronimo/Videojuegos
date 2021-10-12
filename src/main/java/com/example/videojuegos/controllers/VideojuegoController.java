package com.example.videojuegos.controllers;

import com.example.videojuegos.entities.Videojuego;
import com.example.videojuegos.services.CategoriaService;
import com.example.videojuegos.services.EstudioService;
import com.example.videojuegos.services.VideojuegoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.validation.Valid;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;

@Controller
public class VideojuegoController {

    @Autowired
    private VideojuegoService service;
    @Autowired
    private CategoriaService categoriaService;
    @Autowired
    private EstudioService estudioService;

    @GetMapping(value = "/inicio")
    public String inicio(Model model) {
        try {
            List<Videojuego> videojuegos = this.service.findAllByActivo();
            model.addAttribute("videojuegos", videojuegos);
            return "views/inicio";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping(value = "/detalle/{id}")
    public String detalleVideojuego(Model model, @PathVariable("id") long id) {
        try {
            Videojuego videojuego = this.service.findByIdAndActivo(id);
            model.addAttribute("videojuego", videojuego);
            return "views/detalle";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping(value = "/busqueda")
    public String busquedaVideojuego(Model model, @RequestParam(value = "query", required = false) String q) {
        try {
            List<Videojuego> videojuegos = this.service.findByTitle(q);
            model.addAttribute("videojuegos", videojuegos);
            return "views/busqueda";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/crud")
    public String crudVideojuego(Model model) {
        try {
            List<Videojuego> videojuegos = this.service.findAll();
            model.addAttribute("videojuegos", videojuegos);
            return "views/crud";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/formulario/videojuego/{id}")
    public String formularioVideojuego(Model model, @PathVariable("id") long id) {
        try {
            model.addAttribute("categorias", this.categoriaService.findAll());
            model.addAttribute("estudios", this.estudioService.findAll());
            if (id == 0) {
                model.addAttribute("videojuego", new Videojuego());
            } else {
                model.addAttribute("videojuego", this.service.findById(id));
            }
            return "views/formulario/videojuego";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }


    @PostMapping("/formulario/videojuego/{id}")
    public String guardarVideojuego(@RequestParam("archivo") MultipartFile archivo, @Valid @ModelAttribute("videojuego") Videojuego videojuego, BindingResult result, Model model, @PathVariable("id") long id) {
        try {
            if (result.hasErrors()){
                return "views/formulario/videojuego";
            }
            model.addAttribute("categorias", this.categoriaService.findAll());
            model.addAttribute("estudios", this.estudioService.findAll());
            String ruta = "C://Videojuegos/imagenes";
            int index = archivo.getOriginalFilename().indexOf(".");
            String extension = "";
            extension = "." + archivo.getOriginalFilename().substring(index+1);
            String nombreFoto = Calendar.getInstance().getTimeInMillis() + extension;
            Path rutaAbsoluta = id != 0 ? Paths.get(ruta + "//" + videojuego.getImagen()) : Paths.get(ruta + "//" + nombreFoto);
            if (id == 0) {
                if (archivo.isEmpty()){
                    model.addAttribute("imageErrorMsg", "La imagen es requerida");
                    return "views/formulario/videojuego";
                }
                if (!this.validarExtension(archivo)){
                    model.addAttribute("imageErrorMsg", "La extension no es valida");
                    return "views/formulario/videojuego";
                }
                if (archivo.getSize() >= 10000000){
                    model.addAttribute("imageErrorMsg", "El peso excede 10 MB");
                    return "views/formulario/videojuego";
                }
                Files.write(rutaAbsoluta, archivo.getBytes());
                videojuego.setImagen(nombreFoto);
                this.service.saveOne(videojuego);
            } else {
                if (!archivo.isEmpty()){
                    if (!this.validarExtension(archivo)){
                        model.addAttribute("imageErrorMsg", "La extension no es valida");
                        return "views/formulario/videojuego";
                    }
                    if (archivo.getSize() >= 10000000){
                        model.addAttribute("imageErrorMsg", "El peso excede 10 MB");
                        return "views/formulario/videojuego";
                    }
                    Files.write(rutaAbsoluta, archivo.getBytes());
                }
                this.service.updateOne(videojuego, id);
            }
            return "redirect:/crud";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    //este metodo solamente nos lleva a la página de confirmación del videojuego
    @GetMapping("/eliminar/videojuego/{id}")
    public String eliminarVideojuego(Model model, @PathVariable("id") long id) {
        try {
            model.addAttribute("videojuego", this.service.findById(id));
            return "views/formulario/eliminar";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }


    @PostMapping("/eliminar/videojuego/{id}")
    public String desactivarVideojuego(Model model, @PathVariable("id") long id) {
        try {
            this.service.deleteById(id);
            return "redirect:/crud";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }


    public boolean validarExtension(MultipartFile archivo){
        try{
            ImageIO.read(archivo.getInputStream()).toString();
            return true;
        } catch (Exception e){
            return false;
        }
    }

}
