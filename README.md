# The Cloud Media Player

## Projeto realizado no âmbito da unidade curricular "Projecto e Seminário", semestre de verão 2012/2013.

Este projeto consiste no desenvolvimento de uma aplicação _web Mashup_ gratuita para reprodução de conteúdos áudio e vídeo, disponíveis em serviços _cloud_ de alojamento e/ou de reprodução de conteúdos áudio e/ou vídeo.

1. [Web Site](http://thecloudmediaplayer.herokuapp.com)
1. [Proposta de projeto](https://www.dropbox.com/s/larslc6nq7cau5f/pp32186.pdf)
1. [Relatório de progresso](https://www.dropbox.com/s/8gjpnj9l9v25uo4/rp32186.pdf)
1. [Relatório da versão beta]()
1. [Cartaz]()

## Instalação
Para que se possa executar a aplicação em ambiente de desenvolvimento numa máquina local, é necessário cumprir alguns requisitos:

* Ter instalada a versão 7 do [JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html), ou superior;
* Instalar a _framework Play_, seguindo os passos indicados na [documentação](http://www.playframework.com/documentation/2.1.1/Installing);
* Fazer `git clone` deste repositório para uma diretoria à escolha, no sistema de ficheiros do sistema operativo;
* Ter instalada uma instância do SGBD _[PostgreSQL](http://www.postgresql.org/download/)_, versão 9.1, com uma base de dados intitulada "thecloudmediaplayerdb". O servidor de base de dados deve estar à escuta no porto 5432;
* Configurar as seguintes variáveis de ambiente no sistema operativo:
    1. PGUSER - Utilizador da base de dados;
	1. PGPASSWORD - _Password_ do utilizador da base de dados;
	1. THE_CLOUD_MEDIA_PLAYER_APP_SECRET - Chave secreta da aplicação. Utilizada na segurança criptográfica da aplicação.
	
> As configurações da aplicação estão descritas no ficheiro `./conf/application.conf`.

## Iniciar
Através da linha de comandos, aceder à diretoria onde se encontra a aplicação e executar o comando `play run` para executar a aplicação recorrendo à consola de desenvolvimento providenciada pela _framework Play_.
> Para obter mais informações sobre a consola de desenvolvimento, deve consultar o endereço: http://www.playframework.com/documentation/2.1.1/PlayConsole

## Utilizar o IDE Eclipse
Para utilizar o IDE Eclipse para o desenvolvimento da aplicação, deve executar os seguintes passos:

1. Através da linha de comandos, aceder à diretoria onde se encontra a aplicação e executar o comando `play eclipse`;
1. Importar a aplicação para o _workspace_ através do menu **File/Import/General/Existing project...**;

### Efetuar debug
Para efetuar o _debug_ da aplicação, a mesma deve ser executada através do comando `play debug run`. Por defeito, o porto 9999 é usado para efetuar a ligação remota à aplicação para efeitos de _debug_.


> Para obter mais informações sobre a utilização de IDEs em aplicações que utilizem a _framework Play_, deve consultar o seguinte endereço: http://www.playframework.com/documentation/2.1.1/IDE

## Mais informações
Para obter mais informações sobre a _framework Play_, deve consultar o seguinte endereço: http://www.playframework.com.

