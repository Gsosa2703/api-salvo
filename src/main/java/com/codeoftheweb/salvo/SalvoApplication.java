package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;


@SpringBootApplication
public class SalvoApplication {
	

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);

		}

	public class Application extends SpringBootServletInitializer {

		@Bean
		public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository,
										  ScoreRepository scoreRepository) {
			return (args) -> {
				// save a couple of customers
				Player player1 = new Player("j.bauer@ctu.gov", "Juan", passwordEncoder.encode("24"));
				playerRepository.save(player1);
				Player player2 = new Player("c.obrian@ctu.gov", "Chloe", passwordEncoder.encode("42"));
				playerRepository.save(player2);


				Game game1 = new Game();
				gameRepository.save(game1);



				GamePlayer gamePlayer1 = new GamePlayer(player1, game1);
				GamePlayer gamePlayer2 = new GamePlayer(player2, game1);




				Ship ship1 = new Ship("destroyer", Arrays.asList("H2", "H3", "H4"));
				Ship ship2 = new Ship("submarine", Arrays.asList("E1", "F1", "G1"));
				Ship ship3 = new Ship("patrol", Arrays.asList("B2", "B3"));
				Ship ship4 = new Ship("destroyer", Arrays.asList("B5", "C5", "D5"));
				Ship ship5 = new Ship("patrol", Arrays.asList("F1", "F2"));
				Ship ship6 = new Ship("patrol", Arrays.asList("C6", "C7"));
				Ship ship7 = new Ship("submarine", Arrays.asList("A2", "A3", "A4"));

				gamePlayer1.addShip(ship1);
				gamePlayer1.addShip(ship2);
				gamePlayer2.addShip(ship3);
				gamePlayer2.addShip(ship4);


				Salvo salvoes1 = new Salvo(1, Arrays.asList("B5", "C5", "F1"));
				Salvo salvoes2 = new Salvo(1, Arrays.asList("B4", "B5", "B6"));
				Salvo salvoes3 = new Salvo(2, Arrays.asList("F2", "D5"));
				Salvo salvoes4 = new Salvo(2, Arrays.asList("E1", "H3", "A2"));

				gamePlayer1.addSalvoes(salvoes1);
				gamePlayer1.addSalvoes(salvoes3);
				gamePlayer2.addSalvoes(salvoes2);
				gamePlayer2.addSalvoes(salvoes4);


				gamePlayerRepository.save(gamePlayer1);
				gamePlayerRepository.save(gamePlayer2);





//				Score sc3 = new Score(0.5, game2, player3);
//				scoreRepository.save(sc3);
//				Score sc4 = new Score(0.5, game2, player2);
//				scoreRepository.save(sc4);
//				Score sc5 = new Score(1, game3, player4);
//				scoreRepository.save(sc5);
//				Score sc6 = new Score(1, game3, player2);
//				scoreRepository.save(sc6);
//				Score sc7 = new Score(0.5, game4, player3);
//				scoreRepository.save(sc7);
//				Score sc8 = new Score(1, game4, player2);
//				scoreRepository.save(sc8);
//				Score sc9 = new Score(0, game5, player1);
//				scoreRepository.save(sc9);
//				Score sc10 = new Score(1, game5, player3);
//				scoreRepository.save(sc10);


			};
		}
	}
}

