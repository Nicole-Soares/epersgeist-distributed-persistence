package ar.edu.unq.epersgeist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;


@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = {
        "ar.edu.unq.epersgeist",
        "ar.edu.unq.commons"
})
@EnableJpaRepositories
@EnableNeo4jRepositories
@EnableMongoRepositories
public class EpersgeistApp {

    public static void main(String[] args) {
        epersASCII();
        SpringApplication.run(EpersgeistApp.class, args);
    }

    private static void epersASCII() {



        System.out.println();
        System.out.println("__/\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\__/\\\\\\\\\\\\\\\\\\\\\\\\\\____/\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\____/\\\\\\\\\\\\\\\\\\_________/\\\\\\\\\\\\\\\\\\\\\\___        ");
        System.out.println(" _\\/\\\\\\/////////////__\\/\\\\\\/////////\\\\\\_\\/\\\\\\//////////___/\\\\\\///////\\\\\\_____/\\\\\\/////////\\\\\\       ");
        System.out.println("  _\\/\\\\\\_____________\\/\\\\\\_______\\/\\\\\\_\\/\\\\\\_____________\\/\\\\\\_____\\/\\\\\\____\\//\\\\\\______\\///__      ");
        System.out.println("   _\\/\\\\\\\\\\\\\\\\\\\\\\_____\\/\\\\\\\\\\\\\\\\\\\\\\\\\\/__\\/\\\\\\\\\\\\\\\\\\\\\\_____\\/\\\\\\\\\\\\\\\\\\\\\\/______\\////\\\\\\_________     ");
        System.out.println("    _\\/\\\\\\///////______\\/\\\\\\/////////____\\/\\\\\\///////______\\/\\\\\\//////\\\\\\_________\\////\\\\\\______    ");
        System.out.println("     _\\/\\\\\\_____________\\/\\\\\\_____________\\/\\\\\\_____________\\/\\\\\\____\\//\\\\\\___________\\////\\\\\\___   ");
        System.out.println("      _\\/\\\\\\_____________\\/\\\\\\_____________\\/\\\\\\_____________\\/\\\\\\_____\\//\\\\\\___/\\\\\\______\\//\\\\\\__  ");
        System.out.println("       _\\/\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\_\\/\\\\\\_____________\\/\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\_\\/\\\\\\______\\//\\\\\\_\\///\\\\\\\\\\\\\\\\\\/_____");
        System.out.println("        _\\///////////////__\\///______________\\///////////////__\\///________\\///____\\///////////_____");

        System.out.println();
        System.out.println("     ___                                                      _                      #   ___          _   _        |                             |\"|                    ");
        System.out.println("    /\\#/\\          +++        `  ___  '        +++           ((_           >X<       #  <_*_>        '\\\\-//`       |.===.          vvv          _|_|_       `  _ ,  '   ");
        System.out.println("   /(o o)\\        (o o)      -  (O o)  -      (o o)         (o o)         (o o)      #  (o o)         (o o)        {}o o{}        (0~0)         (o o)      -  (o)o)  -  ");
        System.out.println("ooO--(_)--Ooo-ooO--(_)--Ooo-ooO--(_)--Ooo-ooO--(_)--Ooo-ooO--(_)--Ooo-ooO--(_)--Ooo--8---(_)--Ooo-ooO--(_)--Ooo-ooO--(_)--Ooo-ooO--(_)--Ooo-ooO--(_)--Ooo--ooO'(_)--Ooo-");

        System.out.println();
        System.out.println("                  .-. .-')     ('-.     _ (`-.    ('-.  _  .-')    .-')                     .-') _    ('-.                ('-.    .-')            ");
        System.out.println("                  \\  ( OO )  _(  OO)   ( (OO  ) _(  OO)( \\( -O )  ( OO ).                  ( OO ) )  ( OO ).-.          _(  OO)  ( OO ).  ");
        System.out.println("                  ,--. ,--. (,------. _.`     \\(,------.,------. (_)---\\_) .-'),-----. ,--./ ,--,'   / . --. /     ,--.(,------.(_)---\\_) ");
        System.out.println("                  |  .'   /  |  .---'(__...--'' |  .---'|   /`. '/    _ | ( OO'  .-.  '|   \\ |  |\\   | \\-.  \\  .-')| ,| |  .---'/    _ |   ");
        System.out.println("                  |      /,  |  |     |  /  | | |  |    |  /  | |\\  :` `. /   |  | |  ||    \\|  | ).-'-'  |  |( OO |(_| |  |    \\  :` `.   ");
        System.out.println("                  |     ' _)(|  '--.  |  |_.' |(|  '--. |  |_.' | '..`''.)\\_) |  |\\|  ||  .     |/  \\| |_.'  || `-'|  |(|  '--.  '..`''.)  ");
        System.out.println("                  |  .   \\   |  .--'  |  .___.' |  .--' |  .  '.'.-._)   \\  \\ |  | |  ||  |\\    |    |  .-.  |,--. |  | |  .--' .-._)   \\  ");
        System.out.println("                  |  |\\   \\  |  `---. |  |      |  `---.|  |\\  \\ \\       /   `'  '-'  '|  | \\   |    |  | |  ||  '-'  / |  `---.\\       / ");
        System.out.println("                  `--' '--'  `------' `--'      `------'`--' '--' `-----'      `-----' `--'  `--'    `--' `--' `-----'  `------' `-----'  ");

    }
}