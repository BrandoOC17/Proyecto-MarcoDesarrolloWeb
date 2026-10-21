package com.infinitystation.MarcosDesarrolloWeb.service;

import com.infinitystation.MarcosDesarrolloWeb.model.Noticia;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticiaService {

    public List<Noticia> listarNoticias() {
        return List.of(
                new Noticia(1,
                        "Astro Bot: el plataformero que conquistó a la crítica",
                        "El adorable robot de PlayStation se consolidó como uno de los juegos más celebrados de los últimos años. Un viaje lleno de creatividad, referencias y diversión para todas las edades.",
                        "Lanzamientos", "/img/astrobot.png", "fa-rocket"),
                new Noticia(2,
                        "PS5 Pro: qué mejoras ofrece frente a la PS5 estándar",
                        "Más potencia gráfica, mejor trazado de rayos y reescalado por IA. Repasamos qué cambia en la práctica y para quién vale la pena dar el salto.",
                        "Hardware", "/img/play5Pro.png", "fa-microchip"),
                new Noticia(3,
                        "God of War Ragnarok: razones para (re)descubrir la saga nórdica",
                        "Kratos y Atreus enfrentan el fin de los tiempos en una aventura que combina combate brutal, narrativa emotiva y mundos espectaculares.",
                        "Recomendados", "/img/god_of_war_ragnarok.png", "fa-star"),
                new Noticia(4,
                        "DualSense Edge: el control pensado para jugadores competitivos",
                        "Botones traseros, gatillos ajustables y perfiles personalizables. Te contamos cómo sacarle el máximo provecho en tus partidas.",
                        "Hardware", "/img/control_inalambrico_Edge.png", "fa-gamepad"),
                new Noticia(5,
                        "EA SPORTS FC 26: el fútbol virtual sigue ganando terreno",
                        "Nuevas mecánicas, modos online y una comunidad que no deja de crecer. Una entrega que mantiene viva la pasión por el deporte rey.",
                        "Esports", "/img/fc26.png", "fa-futbol"),
                new Noticia(6,
                        "Horizon Zero Dawn Remastered: Aloy vuelve más bella que nunca",
                        "La remasterización mejora gráficos, iluminación y rendimiento para revivir una de las historias más originales de la generación.",
                        "Lanzamientos", "/img/horizont_zero_dawn_remastered.png", "fa-rocket"),
                new Noticia(7,
                        "Gran Turismo 7: consejos para dominar las pistas",
                        "Desde la elección del auto hasta la gestión de neumáticos: trucos sencillos para mejorar tus tiempos por vuelta.",
                        "Consejos", "/img/gran_turismo7.png", "fa-lightbulb"),
                new Noticia(8,
                        "Auriculares Pulse Elite: sonido 3D para una inmersión total",
                        "Audio nítido, micrófono integrado y comodidad para largas sesiones. Una opción ideal para quienes buscan sentirse dentro del juego.",
                        "Hardware", "/img/auriculares_inalambrico_PulseElite.png", "fa-headphones")
        );
    }
}
