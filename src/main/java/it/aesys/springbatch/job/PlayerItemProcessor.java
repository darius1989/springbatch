package it.aesys.springbatch.job;

import it.aesys.springbatch.model.Player;
import org.springframework.batch.item.ItemProcessor;

public class PlayerItemProcessor implements ItemProcessor<Player,Player> {

    @Override
    public Player process(Player item) throws Exception {
        if(item.getPosition().equals("attaccante")) {
            return item;
        }
        return null;
    }
}
