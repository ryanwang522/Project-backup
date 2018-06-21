public class InfoPacket {
    public Player[] players;
    public Platform[] platforms;
    public boolean isPlaying;
    public int winner = -1;

    public InfoPacket(Player[] p, Platform[] plat, boolean start, int winner) {
        this.players = p;
        this.platforms = plat;
        this.isPlaying = start;
        this.winner = winner;
    }

    public void update(boolean start, int winner) {
        this.isPlaying = start;
        this.winner = winner;
    }
}