package expotecsc2018.diego.ifrn.com.expotecsc2018;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.ResultSet;
import java.sql.SQLException;

//expotec.keystore alias expotec, senha m.....$17
public class MainActivity extends AppCompatActivity {
    public String idTrabalho;
    public String matricula;
    public String nomeServidor;
    public String titulo;
    public String resumo;
    public String nota;
    public String id;
    public int avaliadorNUMERO = 0;
    public String avaliacao = "LEMBRE-SE: AS NOTAS SÃO DE 0 A 5 !!! \n\n" +
            "CINCO  (5): Excelente \n" +
            "QUATRO (4): Ótimo \n" +
            "TRÊS   (3): Bom \n" +
            "DOIS   (2): Regular \n" +
            "UM     (1): Ruim \n" +
            "ZERO   (0): Não apresentou o item avaliado \n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }


    public void efetuarLogin(View v) {
        EditText login = (EditText) findViewById(R.id.login);
        EditText senha = (EditText) findViewById(R.id.senha);

        String LOGIN = login.getText().toString();
        String SENHA = senha.getText().toString();
        try {
            Banco b = new Banco();
            ResultSet rs = b.getSenha(LOGIN);
            String cpf = "vazio";
            while (rs.next()) {
                cpf = rs.getString("cpf");
                nomeServidor = rs.getString("nome");
                matricula = rs.getString("matricula");
            }

            String cpfSemPontos = "";
            for (int i = 0; i < cpf.length(); i++) {
                if (cpf.charAt(i) == '.' || cpf.charAt(i) == '-') {
                    //nada
                } else {
                    cpfSemPontos += cpf.charAt(i) + "";
                }
            }
            //Toast.makeText(this, cpfSemPontos, Toast.LENGTH_LONG).show();
            if (SENHA.equals(cpfSemPontos)) {
                String primeiroNome = nomeServidor.split(" ")[0];
                nomeServidor = primeiroNome;

                getSupportActionBar().setTitle(primeiroNome);

                telaTrabalhos(v);
                //clicouAvaliar();
            } else {
                Toast.makeText(this, "Usuário ou Senha Incorretos!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void telaTrabalhos(View v) {
        setContentView(R.layout.trabalhos);
        String trabalhosAvaliados = "";
        String trabalhosPendentes = "";
        EditText campoTrabalhosAvaliados = (EditText) findViewById(R.id.editText3);
        EditText campoTrabalhosPendentes = (EditText) findViewById(R.id.editText4);

        Banco b = new Banco();
        int cont = 0;
        try {

            ResultSet trabs = b.getTrabalhos(this.matricula);

            while (trabs.next()) {

                if(trabs.getString("avaliador1").equals(matricula)){
                    avaliadorNUMERO = 1;
                }
                if(trabs.getString("avaliador2").equals(matricula)){
                    avaliadorNUMERO = 2;
                }
                if(trabs.getString("avaliador3").equals(matricula)){
                    avaliadorNUMERO = 3;
                }
                if(trabs.getString("avaliador4").equals(matricula)){
                    avaliadorNUMERO = 4;
                }

                if (trabs.getString("nota"+avaliadorNUMERO).equals("0")) {
                    cont++;
                    trabalhosPendentes += "TRABALHO: " + trabs.getString("titulo") + "\n";
                    trabalhosPendentes += "LOCAL: " + trabs.getString("local") + "\n";
                    trabalhosPendentes += "HORÁRIO: " + trabs.getString("horario") + "\n\n";
                } else {
                    trabalhosAvaliados += "TRABALHO: " + trabs.getString("titulo") + "\n";
                    trabalhosAvaliados += "LOCAL: " + trabs.getString("local") + "\n";
                    trabalhosAvaliados += "HORÁRIO: " + trabs.getString("horario") + "\n\n";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        campoTrabalhosAvaliados.setText(trabalhosAvaliados);
        campoTrabalhosPendentes.setText(trabalhosPendentes);

        if (cont > 0) {
            Toast.makeText(this, "VOCÊ AINDA TEM TRABALHOS PARA AVALIAR!", Toast.LENGTH_LONG).show();
        }

    }


    public void clicouAvaliar(View v) {

        IntentIntegrator integrador = new IntentIntegrator(this);
        integrador.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrador.setPrompt("scan");
        integrador.setCameraId(0);
        integrador.setBeepEnabled(false);
        integrador.setBarcodeImageEnabled(false);
        integrador.setOrientationLocked(true); //mexi aqui
        integrador.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //ler QRCODE

        IntentResult resultado = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (resultado != null) {
            if (resultado.getContents() == null) {
                Toast.makeText(this, "Scanner cancelado!", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(this, resultado.getContents(), Toast.LENGTH_LONG).show();
                try {
                    this.idTrabalho = resultado.getContents();
                    Banco b = new Banco();
                    ResultSet rs = b.getTrabalhos(matricula, this.idTrabalho);  //nome, id
                    String eixo = "";
                    String notaDesteTrabalhoEsteAvaliador = "";
                    while (rs.next()) {

                        if(rs.getString("avaliador1").equals(matricula)){
                            avaliadorNUMERO = 1;
                        }
                        if(rs.getString("avaliador2").equals(matricula)){
                            avaliadorNUMERO = 2;
                        }
                        if(rs.getString("avaliador3").equals(matricula)){
                            avaliadorNUMERO = 3;
                        }
                        if(rs.getString("avaliador4").equals(matricula)){
                            avaliadorNUMERO = 4;
                        }

                        this.titulo = rs.getString("titulo");
                        this.resumo = rs.getString("resumo");
                        this.id = rs.getString("id");
                        eixo = rs.getString("eixo");
                        notaDesteTrabalhoEsteAvaliador = rs.getString("nota"+avaliadorNUMERO);
                    }
                    if (titulo == null) {
                        Toast.makeText(this, "ESTE TRABALHO NÃO EXISTE OU NÃO DEVE SER AVALIADO POR VOCÊ!", Toast.LENGTH_LONG).show();
                    }else if(!notaDesteTrabalhoEsteAvaliador.equals("0")) {
                        Toast.makeText(this, "VOCÊ JÁ AVALIOU ESTE TRABALHO!", Toast.LENGTH_LONG).show();
                    }else{
                        //Toast.makeText(this, this.titulo , Toast.LENGTH_LONG).show();

                        if (eixo.equals("Pôster")) {
                            Toast.makeText(this, avaliacao, Toast.LENGTH_LONG).show();
                            setContentView(R.layout.avaliarbanner);
                            TextView campoTitulo = (TextView) findViewById(R.id.tituloBanner);

                            campoTitulo.setText(this.titulo);
                        }

                        if (eixo.equals("Comunicação Oral") || eixo.equals("Comunicação Oral")) {
                            Toast.makeText(this, avaliacao, Toast.LENGTH_LONG).show();
                            setContentView(R.layout.avaliarseminario);
                            TextView campoTitulo = (TextView) findViewById(R.id.tituloPesquisaExtensao);

                            campoTitulo.setText(this.titulo);
                        }

                        if (eixo.equals("Mostra Tecnológica")) {
                            Toast.makeText(this, avaliacao, Toast.LENGTH_LONG).show();
                            setContentView(R.layout.avaliarmostra);
                            TextView campoTitulo = (TextView) findViewById(R.id.tituloMostra);

                            campoTitulo.setText(this.titulo);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void salvarNotaBanner(View v) {
        Banco b = new Banco();

        EditText notaAtitude = (EditText) findViewById(R.id.atitudeEdit);
        EditText notaHabilidades = (EditText) findViewById(R.id.habilidadesEdit);
        EditText notaCriatividade = (EditText) findViewById(R.id.criatividadeEdit);
        EditText notaMetodo = (EditText) findViewById(R.id.metodoEdit);
        EditText notaProfundidade = (EditText) findViewById(R.id.profundidadeEdit);
        EditText notaPoster = (EditText) findViewById(R.id.posterEdit);
        EditText notaApresentacao = (EditText) findViewById(R.id.apresentacaoEdit);
        EditText notaRelevancia = (EditText) findViewById(R.id.relevanciaEdit);

        String notaAtitudeT = notaAtitude.getText().toString();
        String notaHabilidadesT = notaHabilidades.getText().toString();
        String notaCriatividadeT = notaCriatividade.getText().toString();
        String notaMetodoT = notaMetodo.getText().toString();
        String notaProfundidadeT = notaProfundidade.getText().toString();
        String notaPosterT = notaPoster.getText().toString();
        String notaApresentacaoT = notaApresentacao.getText().toString();
        String notaRelevanciaT = notaRelevancia.getText().toString();

        double notaAtitudeD = Double.parseDouble(notaAtitudeT);
        double notaHabilidadesD = Double.parseDouble(notaHabilidadesT);
        double notaCriatividadeD = Double.parseDouble(notaCriatividadeT);
        double notaMetodoD = Double.parseDouble(notaMetodoT);
        double notaProfundidadeD = Double.parseDouble(notaProfundidadeT);
        double notaPosterD = Double.parseDouble(notaPosterT);
        double notaApresentacaoD = Double.parseDouble(notaApresentacaoT);
        double notaRelevanciaD = Double.parseDouble(notaRelevanciaT);

        if(notaAtitudeD > 5 ||
         notaAtitudeD  > 5 ||
         notaHabilidadesD  > 5 ||
         notaCriatividadeD  > 5 ||
         notaMetodoD > 5 ||
         notaProfundidadeD  > 5 ||
         notaPosterD  > 5 ||
         notaApresentacaoD  > 5 ||
         notaRelevanciaD  > 5
                ){
            Toast.makeText(this, avaliacao, Toast.LENGTH_LONG).show();
        }else {
            double notaFinalBanner = (notaAtitudeD + notaHabilidadesD * 3 + notaCriatividadeD + notaMetodoD * 3 +
                    notaProfundidadeD * 2 + notaPosterD * 5 + notaApresentacaoD * 3 + notaRelevanciaD * 2);

            //salvar notas no banco
            try {
                b.avaliarBanner(
                        this.idTrabalho, this.matricula,
                        notaAtitudeD,
                        notaHabilidadesD,
                        notaCriatividadeD,
                        notaMetodoD,
                        notaProfundidadeD,
                        notaPosterD,
                        notaApresentacaoD,
                        notaRelevanciaD,
                        notaFinalBanner,
                        avaliadorNUMERO
                );
                b.setNotaTrabalhos(idTrabalho, notaFinalBanner + "", avaliadorNUMERO);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                b.setNotaTrabalhos(this.id, notaFinalBanner + "", avaliadorNUMERO);
                Toast.makeText(this, "TRABALHO AVALIADO COM SUCESSO!", Toast.LENGTH_LONG).show();
                telaTrabalhos(v); //volta para a tela de trabalhos
            } catch (SQLException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }


    public void salvarNotaMostra(View v) {
        Banco b = new Banco();

        EditText notaAtitude = (EditText) findViewById(R.id.atitudeEdit);
        EditText notaHabilidades = (EditText) findViewById(R.id.habilidadesEdit);
        EditText notaCriatividade = (EditText) findViewById(R.id.criatividadeEdit);
        EditText notaMetodo = (EditText) findViewById(R.id.metodoEdit);
        EditText notaProfundidade = (EditText) findViewById(R.id.profundidadeEdit);
        EditText notaRelatorio = (EditText) findViewById(R.id.relatorioEdit);
        EditText notaDiario = (EditText) findViewById(R.id.diarioEdit);
        EditText notaPoster = (EditText) findViewById(R.id.posterEdit);
        EditText notaApresentacao = (EditText) findViewById(R.id.apresentacaoEdit);
        EditText notaRelevancia = (EditText) findViewById(R.id.relevanciaEdit);

        String notaAtitudeT = notaAtitude.getText().toString();
        String notaHabilidadesT = notaHabilidades.getText().toString();
        String notaCriatividadeT = notaCriatividade.getText().toString();
        String notaMetodoT = notaMetodo.getText().toString();
        String notaProfundidadeT = notaProfundidade.getText().toString();
        String notaRelatorioT = notaRelatorio.getText().toString();
        String notaDiarioT = notaDiario.getText().toString();
        String notaPosterT = notaPoster.getText().toString();
        String notaApresentacaoT = notaApresentacao.getText().toString();
        String notaRelevanciaT = notaRelevancia.getText().toString();

        double notaAtitudeD = Double.parseDouble(notaAtitudeT);
        double notaHabilidadesD = Double.parseDouble(notaHabilidadesT);
        double notaCriatividadeD = Double.parseDouble(notaCriatividadeT);
        double notaMetodoD = Double.parseDouble(notaMetodoT);
        double notaRelatorioD = Double.parseDouble(notaRelatorioT);
        double notaDiarioD = Double.parseDouble(notaDiarioT);
        double notaProfundidadeD = Double.parseDouble(notaProfundidadeT);
        double notaPosterD = Double.parseDouble(notaPosterT);
        double notaApresentacaoD = Double.parseDouble(notaApresentacaoT);
        double notaRelevanciaD = Double.parseDouble(notaRelevanciaT);

        if(notaAtitudeD > 5 ||
         notaHabilidadesD > 5 ||
         notaCriatividadeD > 5 ||
         notaMetodoD > 5 ||
         notaRelatorioD > 5 ||
         notaDiarioD > 5 ||
         notaProfundidadeD > 5 ||
         notaPosterD > 5 ||
         notaApresentacaoD > 5 ||
         notaRelevanciaD > 5
                ){
            Toast.makeText(this, avaliacao, Toast.LENGTH_LONG).show();
        }else {

            double notaFinalMostra = (
                    notaAtitudeD +
                            notaHabilidadesD * 3 +
                            notaCriatividadeD +
                            notaMetodoD * 3 +
                            notaProfundidadeD * 2 +
                            notaRelatorioD * 4 +
                            notaDiarioD +
                            notaPosterD +
                            notaApresentacaoD * 2 +
                            notaRelevanciaD * 2
            );

            //salvar notas no banco
            try {
                b.avaliarMostra(this.idTrabalho, this.matricula,
                        notaAtitudeD,
                        notaHabilidadesD,
                        notaCriatividadeD,
                        notaMetodoD,
                        notaProfundidadeD,
                        notaRelatorioD,
                        notaDiarioD,
                        notaPosterD,
                        notaApresentacaoD,
                        notaRelevanciaD,
                        notaFinalMostra,
                        avaliadorNUMERO
                );
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                b.setNotaTrabalhos(this.id, notaFinalMostra + "", avaliadorNUMERO);
                Toast.makeText(this, "TRABALHO AVALIADO COM SUCESSO!", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "NOTA = " + notaFinalMostra, Toast.LENGTH_LONG).show();
                telaTrabalhos(v); //volta para a tela de trabalhos
            } catch (SQLException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void salvarSeminario(View v) {
        Banco b = new Banco();

        EditText notaAtitude = (EditText) findViewById(R.id.atitudeEdit);
        EditText notaHabilidades = (EditText) findViewById(R.id.habilidadesEdit);
        EditText notaCriatividade = (EditText) findViewById(R.id.criatividadeEdit);
        EditText notaMetodo = (EditText) findViewById(R.id.metodoEdit);
        EditText notaProfundidade = (EditText) findViewById(R.id.profundidadeEdit);
        EditText notaRelatorio = (EditText) findViewById(R.id.relatorioEdit);
        EditText notaApresentacaoProjetada = (EditText) findViewById(R.id.slidesEdit);
        EditText notaApresentacao = (EditText) findViewById(R.id.apresentacaoEdit);
        EditText notaRelevancia = (EditText) findViewById(R.id.relevanciaEdit);

        String notaAtitudeT = notaAtitude.getText().toString();
        String notaHabilidadesT = notaHabilidades.getText().toString();
        String notaCriatividadeT = notaCriatividade.getText().toString();
        String notaMetodoT = notaMetodo.getText().toString();
        String notaProfundidadeT = notaProfundidade.getText().toString();
        String notaRelatorioT = notaRelatorio.getText().toString();
        String notaApresentacaoProjetadaT = notaApresentacaoProjetada.getText().toString();
        String notaApresentacaoT = notaApresentacao.getText().toString();
        String notaRelevanciaT = notaRelevancia.getText().toString();

        double notaAtitudeD = Double.parseDouble(notaAtitudeT);
        double notaHabilidadesD = Double.parseDouble(notaHabilidadesT);
        double notaCriatividadeD = Double.parseDouble(notaCriatividadeT);
        double notaMetodoD = Double.parseDouble(notaMetodoT);
        double notaRelatorioD = Double.parseDouble(notaRelatorioT);
        double notaApresentacaoProjetadaD = Double.parseDouble(notaApresentacaoProjetadaT);
        double notaProfundidadeD = Double.parseDouble(notaProfundidadeT);
        double notaApresentacaoD = Double.parseDouble(notaApresentacaoT);
        double notaRelevanciaD = Double.parseDouble(notaRelevanciaT);

        if(  notaAtitudeD > 5 ||
         notaHabilidadesD > 5 ||
         notaCriatividadeD > 5 ||
         notaMetodoD > 5 ||
         notaRelatorioD > 5 ||
         notaApresentacaoProjetadaD > 5 ||
         notaProfundidadeD > 5 ||
         notaApresentacaoD > 5 ||
         notaRelevanciaD > 5
                ){
            Toast.makeText(this, avaliacao, Toast.LENGTH_LONG).show();
        }else {

            double notaFinalPesquisaExtensao = (
                    notaAtitudeD +
                            notaHabilidadesD * 3 +
                            notaCriatividadeD +
                            notaMetodoD * 3 +
                            notaProfundidadeD * 2 +
                            notaRelatorioD * 4 +
                            notaApresentacaoProjetadaD * 2 +
                            notaApresentacaoD * 2 +
                            notaRelevanciaD * 2
            );

            //salvar notas no banco
            try {
                b.avaliarSeminario(this.idTrabalho, this.matricula,
                        notaAtitudeD,
                        notaHabilidadesD,
                        notaCriatividadeD,
                        notaMetodoD,
                        notaProfundidadeD,
                        notaRelatorioD,
                        notaApresentacaoProjetadaD,
                        notaApresentacaoD,
                        notaRelevanciaD,
                        notaFinalPesquisaExtensao,
                        avaliadorNUMERO
                );
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                b.setNotaTrabalhos(this.id, notaFinalPesquisaExtensao + "", avaliadorNUMERO);
                Toast.makeText(this, "TRABALHO AVALIADO COM SUCESSO!", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "NOTA = " + notaFinalPesquisaExtensao, Toast.LENGTH_LONG).show();
                telaTrabalhos(v); //volta para a tela de trabalhos
            } catch (SQLException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    //clicou interrogações ---------------------------------------------------

    public void clicouAtitude(View v) {
        String texto = "O aluno acredita no projeto, demonstra entusiasmo?\n" +
                "O aluno mostrou determinação para superar as dificuldades encontradas no projeto?\n" +
                "O aluno soube selecionar e utilizar materiais ou equipamentos, apropriadamente, para alcançar o resultado final?\n" +
                "O aluno foi capaz de engajar pessoas da sua família, escola ou comunidade no projeto?\n";

        Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
    }

    public void clicouHabilidades(View v) {
        String texto = "O aluno soube utilizar equipamentos, técnicas de laboratório ou sistemas computacionais adequadamente?\n" +
                "O aluno demonstra competência para analisar criticamente dados e informações?\n" +
                "O aluno compreende diferentes pontos de vista, sabe distinguir e compreender situações novas?\n" +
                "É capaz de formular considerações sobre a experiência realizada e compará-la com experiências similares?\n";

        Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
    }

    public void clicouCriatividade(View v) {
        String texto = "O aluno desenvolveu o projeto a partir de uma ideia nova?\n" +
                "O aluno propôs uma resposta original à questão levantada?\n" +
                "O aluno inovou na abordagem (recursos, equipamentos, método) da pesquisa?\n" +
                "O aluno soube relacionar informações de maneira original?\n" +
                "A inovação proposta se aplica para a comunidade?\n";

        Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
    }

    public void clicouMetodo(View v) {
        String texto = "O aluno demonstra capacidade de definir o problema de forma clara?\n" +
                "O aluno propôs uma solução relevante para abordar o problema?\n" +
                "O aluno soube identificar e cumprir as etapas necessárias para desenvolver seu projeto?\n" +
                "O aluno foi capaz de simular, prototipar e testar sua solução?\n" +
                "A solução apresentada tem potencial de se tornar um produto real?\n";

        Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
    }

    public void clicouProfundidade(View v) {
        String texto = "O aluno utiliza mais de uma fonte de informação ou experimento para sua análise?\n" +
                "O aluno conhece outras soluções, teorias ou trabalhos na mesma área?\n" +
                "O aluno consegue vislumbrar possibilidades de continuidade de seu projeto?\n" +
                "O aluno entende os impactos de seu projeto (ambientais, sociais ou econômicos)?\n";

        Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
    }

    public void clicouPoster(View v) {
        String texto = "O pôster está bem estruturado?\n" +
                "O pôster apresenta de forma sucinta os objetivos, o desenvolvimento, os resultados e as conclusões do projeto?\n" +
                "As informações estão organizadas de forma coerente e atrativa?\n";

        Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
    }

    public void clicouApresentacao(View v) {
        String texto = "O aluno foi capaz de organizar as informações relevantes sobre o desenvolvimento e resultados de seu projeto?\n" +
                "O aluno domina o assunto de seu projeto?\n" +
                "O aluno consegue expressar suas ideias de forma objetiva e sintética?\n" +
                "O projeto representa o trabalho conjunto de todos os estudantes? A participação dos integrantes é igualitária\n";

        Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
    }

    public void clicouRelevancia(View v) {
        String texto = "O tema que o aluno escolheu tem relevância no contexto dele?\n" +
                "O projeto desenvolvido tem potencial para transformar a realidade da comunidade em que o aluno vive?\n" +
                "O projeto foi ou é possível de ser colocado em prática?\n";

        Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
    }

    public void clicouRelatorio(View v) {
        String texto = "O texto está bem estruturado?\n" +
                "O objetivo indica a finalidade do projeto, o que pretende realizar?\n" +
                "Os resultados foram adequadamente apresentados e analisados?\n" +
                "A conclusão apresentada é coerente com objetivos, hipóteses e resultados?\n";

        Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
    }

    public void clicouDiario(View v) {
        String texto = "O diário está bem organizado e em ordem cronológica?\n" +
                "O aluno descreveu os procedimentos adotados e os equipamentos utilizados?\n" +
                "O aluno anotou problemas e contradições encontradas, assim como as ideias para resolvê-los?\n" +
                "As contribuições e realizações de cada participante estão bem delineados?\n";

        Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
    }

    public void clicouApresentacaoProjetada(View v) {
        String texto = "A apresentação está bem estruturada, com imagens, gráficos e ausência de textos longos?\n" +
                "Foram apresentados os objetivos, o desenvolvimento, os resultados e as conclusões do projeto de forma satisfatória?\n" +
                "As informações estão organizadas de forma coerente e atrativa?\n";

        Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
    }



}
