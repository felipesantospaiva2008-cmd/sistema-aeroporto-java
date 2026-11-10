import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

class Voo {
    private String numero;
    private String origem;
    private String destino;
    private String horarioPartida;
    private String horarioChegada;
    private int capacidadeMaxima;
    private List<Reserva> passageirosConfirmados;
    private Stack<Reserva> passageirosCheckin; // Utiliza modelo Pilha (LIFO)

    public Voo(String numero, String origem, String destino, String horarioPartida, String horarioChegada, int capacidadeMaxima) {
        this.numero = numero;
        this.origem = origem;
        this.destino = destino;
        this.horarioPartida = horarioPartida;
        this.horarioChegada = horarioChegada;
        this.capacidadeMaxima = capacidadeMaxima;
        this.passageirosConfirmados = new ArrayList<>();
        this.passageirosCheckin = new Stack<>();
    }

    public String getNumero() { return numero; }
    public String getOrigem() { return origem; }
    public String getDestino() { return destino; }
    public String getHorarioPartida() { return horarioPartida; }
    
    public boolean estaCheio() {
        return passageirosConfirmados.size() >= capacidadeMaxima;
    }

    public int vagasDisponiveis() {
        return capacidadeMaxima - passageirosConfirmados.size();
    }

    public List<Reserva> getPassageirosConfirmados() { return passageirosConfirmados; }
    public Stack<Reserva> getPassageirosCheckin() { return passageirosCheckin; }

    public void adicionarPassageiroConfirmado(Reserva reserva) {
        this.passageirosConfirmados.add(reserva);
    }

    public void realizarCheckin(Reserva reserva) {
        this.passageirosCheckin.push(reserva);
    }
}

class Reserva {
    private String nome;
    private int idade;
    private String cpf;
    private String email;
    private String numeroVoo;

    public Reserva(String nome, int idade, String cpf, String email, String numeroVoo) {
        this.nome = nome;
        this.idade = idade;
        this.cpf = cpf;
        this.email = email;
        this.numeroVoo = numeroVoo;
    }

    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getNumeroVoo() { return numeroVoo; }
}

class SistemaAeroporto {
    private List<Voo> voos;
    private Queue<Reserva> filaReservas; // Fila (FIFO) para reservas pendentes

    public SistemaAeroporto() {
        this.voos = new ArrayList<>();
        this.filaReservas = new LinkedList<>();
    }

    public void cadastrarVoo(String numero, String origem, String destino, String partida, String chegada, int capacidade) {
        if (numero == null || !numero.matches("\\d{4}")) {
            System.out.println("Erro: O nÃºmero do voo deve conter obrigatoriamente 4 algarismos.");
            return;
        }

        for (Voo v : voos) {
            if (v.getNumero().equals(numero)) {
                System.out.println("Erro: JÃ¡ existe um voo com este nÃºmero.");
                return;
            }
        }

        Voo novoVoo = new Voo(numero, origem, destino, partida, chegada, capacidade);
        voos.add(novoVoo);
        System.out.println("Voo " + numero + " cadastrado com sucesso!");
    }

    public void realizarReserva(String nome, int idade, String cpf, String email, String numeroVoo) {
        if (!email.contains("@") || !email.contains(".")) {
            System.out.println("Erro: E-mail invÃ¡lido.");
            return;
        }

        Voo vooEncontrado = null;
        for (Voo v : voos) {
            if (v.getNumero().equals(numeroVoo)) {
                vooEncontrado = v;
                break;
            }
        }

        if (vooEncontrado == null) {
            System.out.println("Erro: Voo nÃ£o encontrado.");
            return;
        }

        if (vooEncontrado.estaCheio()) {
            System.out.println("Erro: Este voo jÃ¡ estÃ¡ cheio.");
            return;
        }

        Reserva novaReserva = new Reserva(nome, idade, cpf, email, numeroVoo);
        filaReservas.add(novaReserva);
        System.out.println("Reserva realizada com sucesso! Aguardando aprovaÃ§Ã£o do administrador.");
    }

    public void processarReservasAdmin(Scanner scanner) {
        if (filaReservas.isEmpty()) {
            System.out.println("NÃ£o hÃ¡ reservas pendentes.");
            return;
        }

        System.out.println("\n--- Processamento de Reservas (Administrador) ---");
        Reserva reserva = filaReservas.poll(); // Remove o primeiro da fila (FIFO)

        Voo voo = null;
        for (Voo v : voos) {
            if (v.getNumero().equals(reserva.getNumeroVoo())) {
                voo = v;
                break;
            }
        }

        if (voo == null) {
            System.out.println("Erro: O voo associado a esta reserva nÃ£o existe mais.");
            return;
        }

        System.out.println("Reserva de: " + reserva.getNome() + " para o Voo " + voo.getNumero() + " (" + voo.getOrigem() + " -> " + voo.getDestino() + ")");
        System.out.print("Deseja confirmar esta reserva? (s/n): ");
        String confirmar = scanner.nextLine().trim().toLowerCase();

        if (confirmar.equals("s")) {
            if (voo.estaCheio()) {
                System.out.println("NÃ£o foi possÃ­vel confirmar: o voo atingiu a capacidade mÃ¡xima.");
            } else {
                voo.adicionarPassageiroConfirmado(reserva);
                System.out.println("Reserva confirmada com sucesso!");
            }
        } else {
            System.out.println("Reserva rejeitada/adiada.");
        }
    }

    public void realizarCheckin(String cpf, String numeroVoo) {
        Voo voo = null;
        for (Voo v : voos) {
            if (v.getNumero().equals(numeroVoo)) {
                voo = v;
                break;
            }
        }

        if (voo == null) {
            System.out.println("Voo nÃ£o encontrado.");
            return;
        }

        Reserva reservaEncontrada = null;
        for (Reserva r : voo.getPassageirosConfirmados()) {
            if (r.getCpf().equals(cpf)) {
                reservaEncontrada = r;
                break;
            }
        }

        if (reservaEncontrada == null) {
            System.out.println("Erro: Passageiro nÃ£o encontrado nas reservas confirmadas para este voo.");
            return;
        }

        if (voo.getPassageirosCheckin().contains(reservaEncontrada)) {
            System.out.println("Aviso: Passageiro jÃ¡ realizou o check-in.");
            return;
        }

        voo.realizarCheckin(reservaEncontrada);
        System.out.println("Check-in realizado com sucesso para " + reservaEncontrada.getNome() + "! (Adicionado Ã  pilha de embarque)");
    }

    public void exibirInformacoes() {
        System.out.println("\n================ INFORMAÃÃES DOS VOOS ================");
        
        System.out.println("\n--- Voos DisponÃ­veis (Possuem vagas) ---");
        int dispCount = 0;
        for (Voo v : voos) {
            if (!v.estaCheio()) {
                dispCount++;
                System.out.println("Voo " + v.getNumero() + " | " + v.getOrigem() + " -> " + v.getDestino() + " | Partida: " + v.getHorarioPartida() + " | Vagas: " + v.vagasDisponiveis());
            }
        }
        if (dispCount == 0) System.out.println("Nenhum voo disponÃ­vel com vagas no momento.");

        System.out.println("\n--- Voos com Reservas Pendentes ---");
        List<String> voosComPendencias = new ArrayList<>();
        for (Reserva r : filaReservas) {
            if (!voosComPendencias.contains(r.getNumeroVoo())) {
                voosComPendencias.add(r.getNumeroVoo());
            }
        }
        
        if (!voosComPendencias.isEmpty()) {
            for (String num : voosComPendencias) {
                for (Voo v : voos) {
                    if (v.getNumero().equals(num)) {
                        System.out.println("Voo " + v.getNumero() + " | " + v.getOrigem() + " -> " + v.getDestino());
                    }
                }
            }
        } else {
            System.out.println("Nenhum voo com reservas pendentes.");
        }

        System.out.println("\n--- Voos Cheios ---");
        int cheiosCount = 0;
        for (Voo v : voos) {
            if (v.estaCheio()) {
                cheiosCount++;
                System.out.println("Voo " + v.getNumero() + " | " + v.getOrigem() + " -> " + v.getDestino() + " (Lotado)");
            }
        }
        if (cheiosCount == 0) System.out.println("Nenhum voo cheio no momento.");
        System.out.println("======================================================");
    }
}

public class Produto2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SistemaAeroporto sistema = new SistemaAeroporto();

        // Cadastrando um voo de exemplo para testes
        sistema.cadastrarVoo("2904", "VitÃ³ria (VIX)", "SÃ£o Paulo (GRU)", "10:00", "11:30", 2);

        int opcao = -1;
        do {
            System.out.println("\n--- MENU PRINCIPAL ---");
            System.out.println("1. Cadastrar Voo (Admin)");
            System.out.println("2. Realizar Reserva de Voo");
            System.out.println("3. Processar Reservas Pendentes (Admin)");
            System.out.println("4. Realizar Check-in");
            System.out.println("5. Exibir InformaÃ§Ãµes de Voos");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opÃ§Ã£o: ");
            
            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("OpÃ§Ã£o invÃ¡lida. Digite um nÃºmero.");
                continue;
            }

            switch (opcao) {
                case 1:
                    System.out.print("NÃºmero do Voo (4 dÃ­gitos): ");
                    String num = scanner.nextLine();
                    System.out.print("Origem: ");
                    String orig = scanner.nextLine();
                    System.out.print("Destino: ");
                    String dest = scanner.nextLine();
                    System.out.print("HorÃ¡rio de Partida: ");
                    String part = scanner.nextLine();
                    System.out.print("HorÃ¡rio de Chegada: ");
                    String cheg = scanner.nextLine();
                    System.out.print("Capacidade MÃ¡xima: ");
                    int cap = Integer.parseInt(scanner.nextLine());
                    sistema.cadastrarVoo(num, orig, dest, part, cheg, cap);
                    break;
                case 2:
                    System.out.print("Nome completo: ");
                    String nome = scanner.nextLine();
                    System.out.print("Idade: ");
                    int idade = Integer.parseInt(scanner.nextLine());
                    System.out.print("CPF: ");
                    String cpf = scanner.nextLine();
                    System.out.print("E-mail: ");
                    String email = scanner.nextLine();
                    System.out.print("NÃºmero do Voo desejado (4 dÃ­gitos): ");
                    String numVoo = scanner.nextLine();
                    sistema.realizarReserva(nome, idade, cpf, email, numVoo);
                    break;
                case 3:
                    sistema.processarReservasAdmin(scanner);
                    break;
                case 4:
                    System.out.print("Informe o seu CPF: ");
                    String cpfCheckin = scanner.nextLine();
                    System.out.print("Informe o NÃºmero do Voo: ");
                    String numVooCheckin = scanner.nextLine();
                    sistema.realizarCheckin(cpfCheckin, numVooCheckin);
                    break;
                case 5:
                    sistema.exibirInformacoes();
                    break;
                case 0:
                    System.out.println("Encerrando o sistema...");
                    break;
                default:
                    System.out.println("OpÃ§Ã£o invÃ¡lida. Tente novamente.");
            }
        } while (opcao != 0);

        scanner.close();
    }
}
