package gm;

import java.util.HashMap;

public class ServerEngine implements Messagable{
    public static final String ID_PREFIX = "ID";
    public static final int MAX_LOCX = 99;
    public static final int MAX_LOCY = 99;
    
    int currentId = 0;
    
    private static ServerEngine engine;
    public static ServerEngine getInstance(){
        if(engine == null){
            engine = new ServerEngine();
        }
        return engine;
    }
    
    HashMap<String, ClientHandler> handlers = new HashMap<String, ClientHandler>();
    HashMap<String, Player> players = new HashMap<String, Player>();
    
    public void register(ClientHandler handler){
        String givenId = ID_PREFIX + (++currentId);
        Player player = new Player();
        players.put(givenId, player);
        
        handler.setMessagable(this, givenId);
        GameMessage message = new GameMessage("HELO");
        message.addParam("id", givenId);
        message.addParam("locx", String.valueOf(player.locx));
        message.addParam("locy", String.valueOf(player.locy));
        handler.outgoing(message.toString());
        handlers.put(givenId, handler);   
        
    }
    
    public void unregister(String sourceId){
        handlers.remove(sourceId);
    }

    @Override
    public void incoming(String line, String sourceId) {
        System.out.println(line);
        GameMessage m = GameMessage.parse(line);
        Player player = players.get(sourceId);
        if("MV".equals(m.command)){
            player.move(m.i("dirx"), m.i("diry"));
            sendPlayerPos(sourceId, player);
        }
    }
    
    public void sendPlayerPos(String sourceId, Player player){
        GameMessage m = new GameMessage("POS");
        m.addParam("locx", String.valueOf(player.locx));
        m.addParam("locy", String.valueOf(player.locy));
        handlers.get(sourceId).outgoing(m.toString());
        System.out.println("sending " + m.toString());
    }
}
    
