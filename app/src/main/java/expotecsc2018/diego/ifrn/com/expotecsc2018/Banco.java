package expotecsc2018.diego.ifrn.com.expotecsc2018;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Banco {

    public Statement stmt;
    public Connection conn;

    public Banco() {
        //String url = "jdbc:mysql://profdiego.cswhts9hb6hj.us-west-2.rds.amazonaws.com:3306/expotecsc2018";
        //String usr = "root";
        //String pas = "asmeninasvaopassar";
        String url = "jdbc:mysql://ifrnsc.cswhts9hb6hj.us-west-2.rds.amazonaws.com:3306/expotecsc2018";
        String usr = "ifrnsc";
        String pas = "elasvaopassar";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, usr, pas);
            stmt = conn.createStatement();
        } catch (Exception e) {
            System.out.println("Erro" + e.getMessage());
        }
    }

    public Connection getConn(){
        return this.conn;
    }

    public ResultSet getServidorName(String mat) throws SQLException {
        return stmt.executeQuery("select nome from servidores where matricula='"+ mat + "'");
    }

    public ResultSet getSenha(String mat) throws SQLException {
        return stmt.executeQuery("select * from servidores where matricula='"+ mat + "'");
    }

    public ResultSet getTrabalhos(String matricula, String id) throws SQLException {
        return stmt.executeQuery("select * from trabalhos where id='"+ id +"' AND ( avaliador1 = '"+matricula+"' OR avaliador2 = '"+matricula+"' OR avaliador3 = '"+matricula+"' OR avaliador4 = '"+matricula+"' )");
    }

    public ResultSet getTrabalhoByID(String id) throws SQLException {
        return stmt.executeQuery("select * from trabalhos where id = "+id);
    }

    public ResultSet getTrabalhos(String matricula) throws SQLException {
        return stmt.executeQuery("select * from trabalhos where  avaliador1 = '"+matricula+"' OR avaliador2 = '"+matricula+"' OR avaliador3 = '"+matricula+"' OR avaliador3 = '"+matricula+"'  OR avaliador4 = '"+matricula+"' ");
    }

    public void setNotaTrabalhos(String id, String nota, int avaliadorNUMERO) throws SQLException {
        stmt.executeUpdate("UPDATE trabalhos SET nota"+avaliadorNUMERO+"='"+nota+"' WHERE id='"+id+"'");
    }

    public void avaliarBanner(String idtrabalho, String matricula, double atitude, double habilidades, double criatividade, double metodo,  double profundidade, double poster, double apresentacao, double relevancia, double nota, int avaliadorNUMERO) throws SQLException {
        stmt.executeUpdate("INSERT INTO avaliacaobanner VALUES ('"+idtrabalho+"', '"+matricula+"', "+atitude+", "+habilidades+", "+criatividade+", "+metodo+", "+profundidade+", "+poster+", "+apresentacao+", "+relevancia+" , "+nota+ ")");
        setNotaTrabalhos(idtrabalho, nota+"", avaliadorNUMERO);
    }

    public void avaliarMostra(String idtrabalho, String matricula, double atitude, double habilidades, double criatividade, double metodo,  double profundidade, double relatorio, double diario, double poster, double apresentacao, double relevancia, double nota, int avaliadorNUMERO) throws SQLException {
        stmt.executeUpdate("INSERT INTO avaliacaomostra VALUES ('"+idtrabalho+"', '"+matricula+"', "+atitude+", "+habilidades+", "+criatividade+", "+metodo+", "+profundidade+" , "+relatorio+", "+diario+", "+poster+", "+apresentacao+", "+relevancia+" , "+nota+ ")");
        setNotaTrabalhos(idtrabalho, nota+"", avaliadorNUMERO);
    }

    public void avaliarSeminario(String idtrabalho, String matricula, double atitude, double habilidades, double criatividade, double metodo,  double profundidade, double relatorio, double apresentacaoProjetada, double apresentacao, double relevancia, double nota, int avaliadorNUMERO) throws SQLException {
        stmt.executeUpdate("INSERT INTO avaliacaoseminario VALUES ('"+idtrabalho+"', '"+matricula+"', "+atitude+", "+habilidades+", "+criatividade+", "+metodo+", "+profundidade+" , "+relatorio+", "+apresentacaoProjetada+", "+apresentacao+", "+relevancia+" , "+nota+ ")");
        setNotaTrabalhos(idtrabalho, nota+"", avaliadorNUMERO);
    }


}
