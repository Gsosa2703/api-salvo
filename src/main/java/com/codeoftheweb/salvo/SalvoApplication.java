package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


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


				Ship ship1 = new Ship("Destroyer", Arrays.asList("H2", "H3", "H4"));
				Ship ship2 = new Ship("Submarine", Arrays.asList("E1", "F1", "G1"));
				Ship ship3 = new Ship("Patrol Boat", Arrays.asList("B4", "B5"));
				Ship ship4 = new Ship("Destroyer", Arrays.asList("B5", "C5", "D5"));
				Ship ship5 = new Ship("Patrol Boat", Arrays.asList("F1", "F2"));
				Ship ship6 = new Ship("Patrol Boat", Arrays.asList("C6", "C7"));
				Ship ship7 = new Ship("Submarine", Arrays.asList("A2", "A3", "A4"));

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


				Score sc1 = new Score(1, game1, player1);
				scoreRepository.save(sc1);
				Score sc2 = new Score(0, game1, player2);
				scoreRepository.save(sc2);
				Score sc3 = new Score(0.5, game2, player3);
				scoreRepository.save(sc3);
				Score sc4 = new Score(0.5, game2, player2);
				scoreRepository.save(sc4);
				Score sc5 = new Score(1, game3, player4);
				scoreRepository.save(sc5);
				Score sc6 = new Score(1, game3, player2);
				scoreRepository.save(sc6);
				Score sc7 = new Score(0.5, game4, player3);
				scoreRepository.save(sc7);
				Score sc8 = new Score(1, game4, player2);
				scoreRepository.save(sc8);
				Score sc9 = new Score(0, game5, player1);
				scoreRepository.save(sc9);
				Score sc10 = new Score(1, game5, player3);
				scoreRepository.save(sc10);


			};
		}
	}
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepository;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userName -> {
			Player player = playerRepository.findByUserName(userName);
			if (player != null) {
				return new User(player.getUserName(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unknown user: " + userName);
			}
		});
	}
}

@EnableWebSecurity
@Configuration
class  WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/web/game.html").hasAuthority("USER")
				.antMatchers("/api/game_view/**").hasAuthority("USER")
				.antMatchers("/web/**").permitAll()
				.antMatchers("/api/**").permitAll();

		http.formLogin()
				.usernameParameter("name")
				.passwordParameter("pwd")
				.loginPage("/api/login");

		http.logout().logoutUrl("/api/logout");


		//If you have an X-frame error you only need to add this line to your WebSecurityConfig class.
		//So we need to add the below line of code to be able to see the H2-console in our database.
		http.headers().frameOptions().disable();

		//Be sure to include your login URL in the list of URLs accessible to users who are not logged in!
		//Don't forget to override the default settings that send HTML forms when unauthenticated access happens and when someone logs in or out.
		//Be sure to include your login URL in the list of URLs accessible to users who are not logged in!
		//Don't forget to override the default settings that send HTML forms when unauthenticated access happens and when someone logs in or out.


		//See the Resources for example code. Be sure to follow the example for web services. You want Spring
		// to just sent HTTP success and response codes, no HTML pages.

		// turn off checking for CSRF tokens.CSRF tokens are disabled because supporting them requires a bit of work, and
		// this kind of attack is more typical with regular web page browsing.
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}
}