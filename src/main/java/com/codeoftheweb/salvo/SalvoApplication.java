package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.Models.*;
import com.codeoftheweb.salvo.Repositories.*;
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
				Player player3 = new Player("kim_bauer@gmail.com", "Kim", passwordEncoder.encode("kb"));
				playerRepository.save(player3);
				Player player4 = new Player("t.almeida@ctu.gov", "Tim", passwordEncoder.encode("mole"));
				playerRepository.save(player4);

				Game game1 = new Game();
				gameRepository.save(game1);
				Game game2 = new Game(LocalDateTime.now().plusHours(1));
				gameRepository.save(game2);
				Game game3 = new Game(LocalDateTime.now().plusHours(2));
				gameRepository.save(game3);
				Game game4 = new Game(LocalDateTime.now().plusHours(3));
				gameRepository.save(game4);
				Game game5 = new Game(LocalDateTime.now().plusHours(3));
				gameRepository.save(game5);

				GamePlayer gamePlayer1 = new GamePlayer(player1, game1);
				GamePlayer gamePlayer2 = new GamePlayer(player2, game1);
				GamePlayer gamePlayer3 = new GamePlayer(player3, game2);

				GamePlayer gamePlayer4 = new GamePlayer(player4, game3);

				GamePlayer gamePlayer7 = new GamePlayer(player3, game4);
				GamePlayer gamePlayer8 = new GamePlayer(player2, game4);
				GamePlayer gamePlayer9 = new GamePlayer(player1, game5);
				GamePlayer gamePlayer10 = new GamePlayer(player3, game5);


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
				gamePlayer3.addShip(ship5);
				gamePlayer3.addShip(ship6);
				gamePlayer4.addShip(ship7);

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
				gamePlayerRepository.save(gamePlayer3);
				gamePlayerRepository.save(gamePlayer4);

				gamePlayerRepository.save(gamePlayer7);
				gamePlayerRepository.save(gamePlayer8);
				gamePlayerRepository.save(gamePlayer9);
				gamePlayerRepository.save(gamePlayer10);



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

