package com.infinitystation.MarcosDesarrolloWeb.service;

import com.infinitystation.MarcosDesarrolloWeb.model.Producto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ProductoService {

    private final List<Producto> productos = new ArrayList<>();

    public ProductoService() {

        productos.add(new Producto(
                "cod-bo3",
                "Call of Duty: Black OPS III",
                89.99,
                "PS4",
                "/img/call_of_duty_black_ops3.jpg"
        ));

        productos.add(new Producto(
                "fc26",
                "EA Sports FC 26",
                280.99,
                "PS4",
                "/img/fc26.png"
        ));

        productos.add(new Producto(
                "spiderman2",
                "Marvel Spider-Man 2",
                310.99,
                "PS5",
                "/img/marvel_spider_man2.png"
        ));

        productos.add(new Producto(
                "astrobot",
                "Astro Bot",
                206.99,
                "PS5",
                "/img/astrobot.png"
        ));

        productos.add(new Producto(
                "gow-ragnarok",
                "God of War Ragnarok",
                150.99,
                "PS5",
                "/img/god_of_war_ragnarok.png"
        ));

        productos.add(new Producto(
                "tlou2",
                "The Last of Us Part II",
                179.99,
                "PS4",
                "/img/the_last_of_us_part2.png"
        ));

        productos.add(new Producto(
                "horizon",
                "Horizont Zero Dawn",
                150.99,
                "PS5",
                "/img/horizont_zero_dawn_remastered.png"
        ));

        productos.add(new Producto(
                "gt7",
                "Gran Turismo 7",
                165.99,
                "PS4",
                "/img/gran_turismo7.png"
        ));

        productos.add(new Producto(
                "uncharted4",
                "Uncharted 4",
                69.99,
                "PS4",
                "/img/uncharted4.jpg"
        ));

        productos.add(new Producto(
                "re3",
                "Resident Evil 3",
                120.99,
                "PS4",
                "/img/resident_evil3.jpg"
        ));

        productos.add(new Producto(
                "rdr2",
                "Red Dead Redemption II",
                150.99,
                "PS4",
                "/img/red_dead_redemption2.jpg"
        ));

        productos.add(new Producto(
                "ghost",
                "Ghost of Tsushima",
                170.99,
                "PS4",
                "/img/ghost.jpg"
        ));
    }

    public List<Producto> listarProductos() {
        return productos;
    }

    public Producto buscarPorId(String id) {

        for (Producto producto : productos) {

            if (producto.getId().equals(id)) {
                return producto;
            }
        }

        return null;
    }
}