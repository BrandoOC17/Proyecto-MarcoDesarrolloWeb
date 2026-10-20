
package com.infinitystation.MarcosDesarrolloWeb.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.infinitystation.MarcosDesarrolloWeb.model.Producto;
import com.infinitystation.MarcosDesarrolloWeb.repository.ProductoRepository;

@Service
public class ProductoService {

        // Permite acceder a los productos almacenados en MySQL.
        private final ProductoRepository productoRepository;

        public ProductoService(ProductoRepository productoRepository) {
                this.productoRepository = productoRepository;

                // Registra los productos iniciales solo si la tabla está vacía.
                if (productoRepository.count() == 0) {

                List<Producto> productosIniciales = List.of(
                        new Producto(
                        "Call of Duty: Black OPS III",
                        89.99,
                        "PS4",
                        "/img/call_of_duty_black_ops3.jpg"
                        ),
                        new Producto(
                        "EA Sports FC 26",
                        280.99,
                        "PS4",
                        "/img/fc26.png"
                        ),
                        new Producto(
                        "Marvel Spider-Man 2",
                        310.99,
                        "PS5",
                        "/img/marvel_spider_man2.png"
                        ),
                        new Producto(
                        "Astro Bot",
                        206.99,
                        "PS5",
                        "/img/astrobot.png"
                        ),
                        new Producto(
                        "God of War Ragnarok",
                        150.99,
                        "PS5",
                        "/img/god_of_war_ragnarok.png"
                        ),
                        new Producto(
                        "The Last of Us Part II",
                        179.99,
                        "PS4",
                        "/img/the_last_of_us_part2.png"
                        ),
                        new Producto(
                        "Horizont Zero Dawn",
                        150.99,
                        "PS5",
                        "/img/horizont_zero_dawn_remastered.png"
                        ),
                        new Producto(
                        "Gran Turismo 7",
                        165.99,
                        "PS4",
                        "/img/gran_turismo7.png"
                        ),
                        new Producto(
                        "Uncharted 4",
                        69.99,
                        "PS4",
                        "/img/uncharted4.jpg"
                        ),
                        new Producto(
                        "Resident Evil 3",
                        120.99,
                        "PS4",
                        "/img/resident_evil3.jpg"
                        ),
                        new Producto(
                        "Red Dead Redemption II",
                        150.99,
                        "PS4",
                        "/img/red_dead_redemption2.jpg"
                        ),
                        new Producto(
                        "Ghost of Tsushima",
                        170.99,
                        "PS4",
                        "/img/ghost.jpg"
                        )
                );

                // Guarda los productos; MySQL genera cada ID automáticamente.
                productoRepository.saveAll(productosIniciales);
                }
        }

        // Consulta y devuelve todos los productos almacenados en MySQL.
        public List<Producto> listarProductos() {
                return productoRepository.findAll();
        }

        // Busca un producto mediante su ID numérico.
        public Producto buscarPorId(Long id) {
                return productoRepository.findById(id).orElse(null);
        }
        }
