package eus.ibai.jobs.alerts;

import static java.lang.String.format;

public class TestData {

    public static final String VALID_CHAT_ID = "chatId1";

    public static final String TELEGRAM_GET_UPDATES_OK_RESPONSE = """
            {
              "ok": true,
              "result": []
            }
            """;

    public static final String TELEGRAM_SEND_MESSAGE_OK_RESPONSE = """
            {
              "ok": true,
              "result": {
                "message_id": 132,
                "sender_chat": {
                  "id": "chat1",
                  "title": "ChatTitle",
                  "username": "chatUser",
                  "type": "channel"
                },
                "chat": {
                  "id": "chat1",
                  "title": "ChatTitle",
                  "username": "ChatUser",
                  "type": "channel"
                },
                "date": 0,
                "text": " SITE UPDATED Complutense: Profesor Ayudante Doctor Total Jobs: 15  New Jobs: - Convocatoria publicada en el BOUC de 18 de octubre de 2022",
                "entities": [
                  {
                    "offset": 4,
                    "length": 13,
                    "type": "bold"
                  },
                  {
                    "offset": 21,
                    "length": 37,
                    "type": "text_link",
                    "url": "https://www.ucm.es/profesor-ayudante-doctor"
                  },
                  {
                    "offset": 21,
                    "length": 37,
                    "type": "bold"
                  },
                  {
                    "offset": 87,
                    "length": 58,
                    "type": "text_link",
                    "url": "https://www.ucm.es/convocatoria-publicada-en-el-bouc-de-18-de-octubre-de-2022-3"
                  },
                  {
                    "offset": 148,
                    "length": 57,
                    "type": "text_link",
                    "url": "https://www.ucm.es/convocatoria-publicada-en-el-bouc-de-3-de-octubre-de-2022-4"
                  }
                ]
              }
            }
            """;

    public static final String TELEGRAM_SEND_MESSAGE_BAD_REQUEST_RESPONSE = """
            {
              "ok" : false,
              "error_code" : 400,
              "description": "Bad Request: chat not found"
            }
            """;

    public static final String NEW_RELIC_SEND_METRICS_SUCCESS_RESPONSE = """
            {
              "requestId": "00000000-0000-0000-0000-000000000000"
            }
            """;

    public static final String JOB_SITE_1_NAME = "JobSite1";

    public static final String JOB_SITE_1_PATH = "/job-site-1";

    public static final String JOB_SITE_1_URL_FORMAT = "%s" + JOB_SITE_1_PATH;

    public static final String NON_EXISTENT_JOB_SITE_PATH = "/non-existent-job-site";

    public static final String NON_EXISTENT_JOB_SITE_URL_FORMAT = "%s" + NON_EXISTENT_JOB_SITE_PATH;

    public static final String JOB_1_TITLE = "Job 1";

    public static final String JOB_1_URL_FORMAT = "%s/job1/details";

    public static final String JOB_SITE_2_NAME = "JobSite2";

    public static final String JOB_2_TITLE = "Job 2";

    public static final String JOB_2_URL_FORMAT = "%s/job2/details";

    private static final String JOB_2_RELATIVE_URL = "/job2/details";

    public static String jobSite1Response(String wiremockBaseUrl) {
        return format("""
                <html><body>
                    <ul class="menu_pag">
                        <li><a href="%s">%s</a></li>
                        <li><a href="%s">%s</a></li>
                    </ul>
                </body></html>
                """, format(JOB_1_URL_FORMAT, wiremockBaseUrl), JOB_1_TITLE, JOB_2_RELATIVE_URL, JOB_2_TITLE);
    }

    public static final String UCM_PROFESOR_AYUDANTE_DOCTOR_RESPONSE = """
            <!DOCTYPE html>
            <html lang="es">
            <head>
            	<meta charset="UTF-8" />
            	<meta http-equiv="X-UA-Compatible" content="IE=edge">
                <meta name="viewport" content="width=device-width, initial-scale=1">
            	<title>Profesor Ayudante Doctor | Universidad Complutense de Madrid</title>
            	
            	<meta name="description" content="La mayor universidad presencial de España, con la mejor oferta educativa. En Madrid, rodeada de zonas verdes y con una intensa vida cultural y deportiva." />
            	<meta name="keywords" content="Universidad Complutense de Madrid, Complutense, UCM, Educación, Innovación, Universidad de excelencia, Formación, Grado, Máster, Doctorado, Postgrado" />
            	<link rel="apple-touch-icon" sizes = "192x192" href="/themes/ucm16/media/img/favicon-192.png" />
            	<link rel="shortcut icon" href="/themes/ucm16/media/img/logoucm.ico" />
            	<link rel="icon" href="/themes/ucm16/media/img/logoucm.ico" />
            	<link type="text/css" media="screen" rel="stylesheet" href="/themes/ucm16/css/bootstrap.css" />
            	<link type="text/css" media="all" rel="stylesheet" href="/themes/ucm16/css/font-awesome.min.css" />
            	<link type="text/css" media="screen" rel="stylesheet" href="/themes/ucm16/css/ucm-theme.css" />
            	<link type="text/css" media="screen" rel="stylesheet" href="/themes/ucm16/css/ucm.css" />
            	<link type="text/css" media="screen" rel="stylesheet" href="/themes/ucm16/css/ucm-wg.css" />
            	<link type="text/css" media="print" rel="stylesheet" href="/themes/ucm16/css/print.css" />
            	</head>
            <body>
            	<header>
            		<div id="barra">
            			<div class="container">
            								
            				<ul class="ul-menu">
            					<li>
            				        <div id="buscador">
            				            <form action="https://www.ucm.es/buscador" method="get" class="posicion_buscar form-inline" id="formbuscador">
            								<label for="search" class="search_label">Buscar en la web</label>
            								<input type="search" id="search" name="search" placeholder="Buscar en la web" required />
            								<button type="submit" class="botonbusca" id="btsearch" title="Buscar en la web" aria-label="Buscar en la web"><span class="fa fa-search" aria-hidden="true" title="Buscar en la web"></span></button>
            							</form>
            								<button class="botoncerrar" id="cerrar_buscador" aria-label="Cerrar buscador"><span class="fa fa-times" aria-hidden="true" title="Cerrar buscador"></span></button>
            					    </div>
            					</li>
            					<li>
            						<nav class="navbar" role="navigation" id="menusuperior">
            							<div class="navbar-header">
            								<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-sup-collapse" id="collapse-personal">
            									<span class="sr-only">Desplegar navegación</span>
            									<span class="fa fa-user fa-2x"></span>
            					            </button>
            							</div>
            							<div id="contenido-menusuperior" class="collapse navbar-collapse navbar-sup-collapse">
            								<ul class="nav navbar-nav" >
            														<li class="iniciasesion"><a href="https://www.ucm.es/login_sso/" title="Navegar identificado">Navegar identificado</a></li>																	
            								</ul>
            							</div>
            						</nav>				
            					</li>
            					<li>
            						<ul id="lg_items">
            							
            				<li><a href="https://www.ucm.es/english" title="English: Complutense University of Madrid" class="enlace"><img src="/themes/ucm3/media/img/banderas/en.png" title="English" alt="English" /></a></li>						</ul>
            					</li>
            				</ul>
            			</div>
            		</div>
            		
            		<div class="container" id="cabecera">
            			<div class="row">
            				<div class="col-lg-5 col-sm-5 col-xs-12" id="logo">
            					<a href="https://www.ucm.es/" title="Universidad Complutense de Madrid">
            						<img src="/themes/ucm16/media/img/logo.png" alt="ucm" title="Universidad Complutense de Madrid" />
            					</a>
            				</div>
            				<div class="col-lg-7 col-sm-7 col-xs-12" id="titulo_website">
            					<h1 class="web_title_ucm"><a href="https://www.ucm.es/" title="Portada - Universidad Complutense de Madrid">Universidad Complutense de Madrid</a></h1><h1 class="web_title"><a href="https://www.ucm.es/informacion/pagina-principal" title="Universidad Complutense de Madrid con Ucrania"><img src="https://www.ucm.es/nodes/web/org/ucm/media/img/ucm-ucrania.png" alt="Logo Universidades Españolas con Ucrania" title="Universidades Españolas con Ucrania"></a></h1>					
            				</div>
            			</div>
            		</div>
            			</header>
            	
            	<nav class="container navbar navbar-default" role="navigation" id="menu">
            		<div class="navbar-header">
            			<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-ex1-collapse" id="botonmenu">
            				<span class="sr-only">Desplegar navegación</span>
            				<span class="fa fa-bars"></span>
            			</button>
            		</div>
            \s
            		<div class="collapse navbar-collapse navbar-ex1-collapse">
            			
            			<ul class="nav nav-justified" id="contenidomenu">
            				<li class="dropdown resalte lead">
            					<a title="UCM" href="/laucm" class="dropdown-toggle" data-toggle="dropdown">UCM</a>
            					<ul class="dropdown-menu" role="menu">
            						<li><a title="Gobierno" href="/gobierno">Gobierno</a></li>
            						<li><a title="Facultades" href="/facultades">Facultades</a></li>
            						<li><a title="Departamentos" href="/departamentos-ucm">Departamentos</a></li>
            						<li><a title="Otros Centros" href="/centros">Otros Centros</a></li>
            						<li><a title="Servicios" href="/servicios_1">Servicios</a></li>
            						<li><a title="Transparencia" href="/portaldetransparencia">Transparencia</a></li>
            						<li><a title="Calidad" href="https://www.ucm.es/opc/">Calidad</a></li>
            						<li><a title="Igualdad" href="https://www.ucm.es/unidaddeigualdad/">Igualdad</a></li>
            						<li><a title="Alumni" href="https://alumni.ucm.es/">Alumni</a></li>
            						<li><a title="Emprendimiento" href="https://www.ucm.es/compluemprende">Emprendimiento</a></li>
            						<li><a href="/laucm" title="UCM" aria-label="UCM"><span class="fa fa-plus-circle" aria-hidden="true" title="UCM"><em class="mas">+</em></span></a></li>
            					</ul>
            				</li>
            				<li class="dropdown resalte lead">
            					<a title="Estudiar" href="/estudiar" class="dropdown-toggle" data-toggle="dropdown">Estudiar</a>
            					<ul class="dropdown-menu" role="menu">
            						<li><a title="Estudios" href="/estudios-ofertados">Estudios</a></li>
            						<li><a title="Pruebas de acceso a Grado" href="https://www.ucm.es/pruebas-de-acceso">Pruebas de acceso a Grado</a></li>
            						<li><a title="Admisión a la Universidad" href="/acceso">Admisión a la Universidad</a></li>
            						<li><a title="Matrícula" href="/matricula-estudios-oficiales">Matrícula</a></li>
            						<li><a title="Prácticas y empleo" href="https://www.ucm.es/ope">Prácticas y empleo</a></li>
            						<li><a title="Becas y ayudas" href="/becas-ayudas">Becas y ayudas</a></li>
            						<li><a title="Estudiantes internacionales" href="https://www.ucm.es/informacion/estudiantes-internacionales">Estudiantes internacionales</a></li>
            						<li><a title="Formación Permanente" href=" https://www.ucm.es/cfp/ ">Formación Permanente</a></li>
            						<li><a title="Cursos de Verano El Escorial" href="https://cursosveranoucm.com">Cursos de Verano El Escorial</a></li>
            						<li><a title="Escuela Complutense de Verano" href="http://www.ucm.es/escuelacomplutense/">Escuela Complutense de Verano</a></li>
            						<li><a href="/estudiar" title="Estudiar" aria-label="Estudiar"><span class="fa fa-plus-circle" aria-hidden="true" title="Estudiar"><em class="mas">+</em></span></a></li>
            					</ul>
            				</li>
            				<li class="dropdown resalte lead">
            					<a title="Investigar" href="/investigar" class="dropdown-toggle" data-toggle="dropdown">Investigar</a>
            					<ul class="dropdown-menu" role="menu">
            						<li><a title="Investigación" href="/investigacion">Investigación</a></li>
            						<li><a title="Servicio de Investigación" href="/servicio-de-investigacion">Servicio de Investigación</a></li>
            						<li><a title="Servicio de Administración de Personal Investigador" href="/pinves">Servicio de Administración de Personal Investigador</a></li>
            						<li><a title="OTRI" href="http://www.ucm.es/otri">OTRI</a></li>
            						<li><a title="HRS4R" href="http://www.ucm.es/hrs4r/">HRS4R</a></li>
            						<li><a title="Ediciones Complutense" href="https://www.ucm.es/ediciones-complutense">Ediciones Complutense</a></li>
            						<li><a title="Biblioteca" href="http://biblioteca.ucm.es">Biblioteca</a></li>
            						<li><a href="/investigar" title="Investigar" aria-label="Investigar"><span class="fa fa-plus-circle" aria-hidden="true" title="Investigar"><em class="mas">+</em></span></a></li>
            					</ul>
            				</li>
            				<li class="dropdown resalte lead">
            					<a title="Internacional" href="/internacional" class="dropdown-toggle" data-toggle="dropdown">Internacional</a>
            					<ul class="dropdown-menu" role="menu">
            						<li><a title="Portada" href="https://www.ucm.es/internacional">Portada</a></li>
            						<li><a title="Noticias" href="/noticias-int">Noticias</a></li>
            						<li><a title="Convocatorias" href="/internacional-convocatorias">Convocatorias</a></li>
            						<li><a title="Movilidad" href="/movilidad-mobility">Movilidad</a></li>
            						<li><a title="Programas Europeos" href="/programas-europeos">Programas Europeos</a></li>
            						<li><a title="Programas UCM" href="/convenios">Programas UCM</a></li>
            						<li><a title="Una Europa" href="/una-europa-2">Una Europa</a></li>
            						<li><a title="Contacto" href="/contacto-rrii">Contacto</a></li>
            						<li><a href="/internacional" title="Internacional" aria-label="Internacional"><span class="fa fa-plus-circle" aria-hidden="true" title="Internacional"><em class="mas">+</em></span></a></li>
            					</ul>
            				</li>
            				<li class="dropdown resalte lead">
            					<a title="Vida universitaria" href="/sociedad" class="dropdown-toggle" data-toggle="dropdown">Vida universitaria</a>
            					<ul class="dropdown-menu" role="menu">
            						<li><a title="Agenda" href="/agenda-1">Agenda</a></li>
            						<li><a title="Cultura y museos" href="http://www.ucm.es/cultura">Cultura y museos</a></li>
            						<li><a title="Deportes" href="https://www.ucm.es/deportesucm">Deportes</a></li>
            						<li><a title="Encuentros UCM" href="http://www.ucm.es/encuentros">Encuentros UCM</a></li>
            						<li><a title="Tienda Complutense" href="https://universidadcomplutense.shop/es/">Tienda Complutense</a></li>
            						<li><a href="/sociedad" title="Vida universitaria" aria-label="Vida universitaria"><span class="fa fa-plus-circle" aria-hidden="true" title="Vida universitaria"><em class="mas">+</em></span></a></li>
            					</ul>
            				</li>
            			</ul>			
            		</div>
            	</nav>
            	
            	<main class="container">
            		<ol class="breadcrumb"><li><a href="/" title="Portada">Portada</a></li><li><a href="/laucm" title="La Universidad Complutense">La Universidad Complutense</a></li><li class="active">[...]</li><li><a href="/personal-docente-e-investigador-contratado" title="Personal docente e investigador contratado">Personal docente e investigador contratado</a></li><li class="active">Profesor Ayudante Doctor</li></ol>
            <h1>Profesor Ayudante Doctor</h1>
            <div class="row">
                <div class="col-sm-12 col-xs-12" id="marco_01">
            	<div class="column" id="lista_1">
            	
            	<div class="wg_txt">
            				<div><div>Advertencia:&nbsp;<em>La informaci&oacute;n contenida en estas p&aacute;ginas es meramente informativa y no originar&aacute; derechos ni expectativas de derechos, de acuerdo con el art&iacute;culo 14 del Decreto 21/2002 de 24 de enero (BOCM de 5 de febrero de 2002) que regula la atenci&oacute;n al ciudadano en la Comunidad de Madrid para el ejercicio de sus derechos, cumplimiento de sus obligaciones y acceso a los servicios p&uacute;blicos.</em></div>
            <div>&nbsp;</div>
            <div style="text-align: center;">&nbsp;<a href="https://www.ucm.es/file/3758">Acuerdo del Consejo de Gobierno de 27 de octubre de 2020 por el que se regula el r&eacute;gimen&nbsp; de actuaciones de las comisiones de selecci&oacute;n de personal docente</a></div>
            <div>&nbsp;</div>
            <p><a href="https://www.ucm.es/file/4131">DISPOSICI&Oacute;N REGULADORA DEL PROCESO DE SELECCI&Oacute;N</a> (Consejo de Gobierno de 28 de septiembre 2021)</p>
            <p><a href="/data/cont/media/www/pag-8347/3485.pdf">DISPOSICI&Oacute;N REGULADORA DEL PROCESO DE SELECCI&Oacute;N </a>(para convocatorias publicadas a partir del 4 de diciembre de 2019)</p>
            <p><a href="/data/cont/media/www/pag-8347/3277.pdf">DISPOSICI&Oacute;N REGULADORA DEL PROCESO DE SELECCI&Oacute;N</a> (para convocatorias publicadas a partir del 29 de marzo de 2019)</p>
            				</div>
            	<div class="spacer"><br/></div>
            	</div>	</div>
            </div>
            <div class="row">
            	<div class="col-sm-6 col-xs-12"  id="marco_02">
            		<div class="column" id="lista_2">
            		
            	<div class="wg_nav">
            				<h2>CONVOCATORIAS</h2><ul class="menu_pag "><li><a  href="https://www.ucm.es/convocatoria-publicada-en-el-bouc-de-3-de-octubre-de-2022-4" >Convocatoria publicada en el BOUC de 3 de octubre de 2022</a></li><li><a  href="https://www.ucm.es/convocatoria-publicada-en-el-bouc-de-27-de-septiembre-de-2022-3" >Convocatoria publicada en el BOUC de 27 de septiembre de 2022</a></li><li><a  href="https://www.ucm.es/convocatoria-publicada-en-el-bouc-de-15-de-septiembre-de-2022-2" >Convocatoria publicada en el BOUC de 15 de septiembre de 2022</a></li><li><a  href="https://www.ucm.es/convocatoria-publicada-en-el-bouc-de-21-de-julio-de-2022-1" >Convocatoria publicada en el BOUC de 21 de julio de 2022</a></li><li><a  href="https://www.ucm.es/convocatoria-publicada-en-el-bouc-de-7-de-julio-de-2022-3" >Convocatoria publicada en el BOUC de 7 de julio de 2022</a></li><li><a  href="https://www.ucm.es/convocatoria-publicada-en-el-bouc-de-16-de-junio-de-2022-1" >Convocatoria publicada en el BOUC de 16 de junio de 2022</a></li><li><a  href="https://www.ucm.es/convocatoria-publicada-en-el-bouc-de-2-de-junio-de-2022-3" >Convocatoria publicada en el BOUC de 2 de junio de 2022</a></li><li><a  href="https://www.ucm.es/convocatoria-publicada-en-el-bouc-de-18-de-mayo-de-2022-3" >Convocatoria publicada en el BOUC de 18 de mayo de 2022</a></li><li><a  href="https://www.ucm.es/convocatoria-publicada-en-el-bouc-de-5-de-mayo-de-2022-4" >Convocatoria publicada en el BOUC de 5 de mayo de 2022</a></li><li><a  href="https://www.ucm.es/convocatoria-publicada-en-el-bouc-de-20-de-abril-de-2022" >Convocatoria publicada en el BOUC de 20 de abril de 2022</a></li><li><a  href="https://www.ucm.es/convocatoria-publicada-en-el-bouc-de-23-de-marzo-de-2022" >Convocatoria publicada en el BOUC de 23 de marzo de 2022</a></li><li><a  href="https://www.ucm.es/convocatoria-publicada-en-el-bouc-1-de-marzo-de-2022" >Convocatoria publicada en el BOUC 1 de marzo de 2022</a></li><li><a  href="https://www.ucm.es/convocatoria-publicada-en-el-bouc-de-14-de-febrero-de-2022-1" >Convocatoria publicada en el BOUC de 14 de febrero de 2022</a></li><li><a  href="https://www.ucm.es/convocatoria-publicada-en-el-bouc-de-31-de-julio-de-2020" >Convocatoria publicada en el BOUC de 31 de julio de 2020</a></li></ul>
            	</div>		</div>
            	</div>
            	<div class="col-sm-6 col-xs-12" id="marco_03">
            		<div class="column" id="lista_3">
            		
            	<div class="wg_txt">
            				<div><h2>Presentación de solicitudes de participación</h2><ul>
            <li>&nbsp;&nbsp;<strong>Modelo de solicitud</strong>: Instancia (<a href="https://sede.ucm.es/proceso-selectivo-profesor-ayudante-doctor"><strong>SEDE ELECTR&Oacute;NICA</strong></a>-<a href="https://www.ucm.es/file/instancia-concursar-a-plazas-pad-6-febrero-20"><strong>WORD</strong></a>-<a href="https://www.ucm.es/file/instancia-concursar-a-plazas-pad-6-febrero-20-1"><strong>PDF</strong>)</a>.&nbsp;Pueden incluir tantos anexos como consideren oportunos.</li>
            </ul>
            <p style="padding-left: 30px;"><strong><span class="txt_fondo_gris txt_fondo_rojo txt_fondo_naranja"><a href="https://www.ucm.es/file/guia_presentacion_solicitudes-plazas-pdi_def?ver=n">Gu&iacute;a para la presentaci&oacute;n de solicitudes.</a></span></strong></p>
            <ul>
            <li style="text-align: justify;"><strong>Modelo de declaraci&oacute;n jurada</strong>: <a href="https://www.ucm.es/file/decl-jurada-situacion.docx-3?ver">WORD</a> - <a href="https://www.ucm.es/file/decl-jurada-situacion-3?ver">PDF</a></li>
            <li style="text-align: justify;"><strong style="font-size: 1em;">Documentaci&oacute;n a presentar</strong><span style="font-size: 1em;">: fotocopia del DNI, fotocopia del t&iacute;tulo de Doctor o documento de haber abonado los derechos de expedici&oacute;n, documento de acreditaci&oacute;n positiva por parte de la ANECA, ACAP&nbsp;o&nbsp;Agencias que tengan convenio de&nbsp;reconocimiento con alguna de ellas y documentaci&oacute;n acreditativa&nbsp;de los m&eacute;ritos rese&ntilde;ados.&nbsp;</span>Los candidatos que deban aportar la certificaci&oacute;n oficial de nivel C1 de espa&ntilde;ol y no dispongan de ella pueden solicitar su evaluaci&oacute;n en el <a style="background-color: #ffffff; font-size: 1em;" href="https://www.ucm.es/ccee/">Centro Complutense para la Ense&ntilde;anza del Espa&ntilde;ol</a><span style="font-size: 1em;">: </span><a style="background-color: #ffffff; font-size: 1em;" href="mailto:secreacadccee@ucm.es">secreacadccee@ucm.es.</a>&nbsp;<span>Relaci&oacute;n&nbsp;</span>
            <p>&nbsp;de m&eacute;ritos, numerada y ordenada según la instancia de solicitud, de todos los méritos que se acreditan.</p>
            </li>
            <li><a href="https://bouc.ucm.es/pdf/4275.pdf"><strong>Derechos de examen</strong> para participar en convocatorias de personal docente contratado de la UCM (convocatorias publicadas a partir del 21 de febrero).</a></li>
            <li style="text-align: justify;"><strong>Lugar de presentaci&oacute;n</strong>:&nbsp;
            <p>Mediante la instancia que figura en la Sede Electr&oacute;nica de la UCM (https://sede.ucm.es/proceso-selectivo-profesor-ayudante-doctor), o por cualquiera de los procedimientos establecidos en el art&iacute;culo 16.4 de la Ley 39/2015, del Procedimiento Administrativo Com&uacute;n de las Administraciones P&uacute;blicas (en lo sucesivo LPACAP), seg&uacute;n modelo disponible en esta p&aacute;gina web.</p>
            </li>
            <li style="text-align: justify;"><strong>Plazo de presentaci&oacute;n</strong>: 15 dias h&aacute;biles a partir del siguiente al de publicaci&oacute;n de la convocatoria o al de la fecha que se fije en la misma.</li>
            </ul>
            				</div>
            	<div class="spacer"><br/></div>
            	</div>
            	<div class="wg_txt">
            				<div><h3><a href="https://www.ucm.es/documentos-comisiones-PAD">Documentos para las comisiones de selecci&oacute;n</a></h3>
            				</div>
            	<div class="spacer"><br/></div>
            	</div>
            	<div class="wg_txt">
            				<div><h2>Documentación a presentar solo por los candidatos propuestos</h2><ul>
            <li><a href="https://www.ucm.es/file/f-02.-formulario-recogida-datos-pago-haberes-y-domicilio"><strong>DOC A</strong></a><a href="https://www.ucm.es/data/cont/media/www/pag-8344/recogida%20datos.pdf">:</a>&nbsp;Datos personales</li>
            <li>Fotocopia DNI</li>
            <li>N&ordm; de la Seguridad Social</li>
            <li>Fotocopia de la Titulaci&oacute;n apta&nbsp;prevista en la convocatoria.</li>
            <li>Fotocopia compulsada del t&iacute;tulo de Doctor o documento de haber abonado los derechos de expedici&oacute;n.</li>
            <li>Fotocopia compulsada de la acreditaci&oacute;n positiva por parte de la ANECA, ACAP&nbsp;o Agencias que tengan convenio de&nbsp;reconocimiento con alguna de ellas.</li>
            <li><a href="/data/cont/media/www/pag-8347/pad%20decl.%20jurada%20modif..pdf"><strong>Declaraci&oacute;n jurada</strong>&nbsp;de no haber agotado el plazo m&aacute;ximo de duraci&oacute;n en un contrato de la misma categor&iacute;a en cualquier Universidad</a></li>
            </ul>
            				</div>
            	<div class="spacer"><br/></div>
            	</div>		</div>
            	</div>
            </div>	</main>	
            	
            	<nav class="container text-center">
            		<ul class="redes">
            			<li><a id="link_yb" href="http://www.youtube.com/ucomplutensemadrid" class="redsocial" target="_blank"><img alt="youtube" src="/themes/ucm16/media/img/youtube.png" /></a></li>
            			<li><a id="link_fb" href="https://www.facebook.com/UniComplutense" class="redsocial" target="_blank"><img alt="facebook" src="/themes/ucm16/media/img/facebook.png" /></a></li>
            			<li><a id="link_tw" href="http://twitter.com/unicomplutense" class="redsocial" target="_blank"><img alt="twitter" src="/themes/ucm16/media/img/twitter.png" /></a></li>
            			<li><a id="link_lnk" href="https://www.linkedin.com/company/universidadcomplutense" class="redsocial" target="_blank"><img alt="linkedin" src="/themes/ucm16/media/img/linkedin.png" /></a></li>
            			<li><a id="link_in" href="https://www.instagram.com/uni.complutense/" class="redsocial" target="_blank"><img alt="instagram" src="/themes/ucm16/media/img/instagram.png" /></a></li>
            		</ul>
            	</nav>	
            	<footer id="pie">
            		<div class="container">
            			<div class="row">
            				<div class="col-sm-4 col-xs-12">
            					<nav id="pie_1">
            						<ul>
            						<li id="175"><a href="/empleo-ucm" title="Empleo UCM">Empleo UCM</a></li>
            						<li id="75"><a href="/fundacion" title="Fundación General">Fundación General</a></li>
            						</ul>
            					</nav>
            				</div>
            				<div class="col-sm-4 col-xs-12">
            					<nav id="pie_2">
            						<ul>
            						<li id="1206"><a href="http://www.ucm.es/gespacios" title="Alquiler de espacios">Alquiler de espacios</a></li>
            						<li id="177"><a href="http://www.fpcm.es/" title="Parque Científico">Parque Científico</a></li>
            						</ul>
            					</nav>
            				</div>
            				<div class="col-sm-4 col-xs-12">
            					<nav id="pie_3">
            						<ul>
            						<li id="12"><a href="http://www.universia.es/" title="Universia España">Universia España</a></li>
            						<li id="1361"><a href="https://www.ucm.es/buzon-de-sugerencias-y-quejas" title="Sugerencias y Quejas">Sugerencias y Quejas</a></li>
            						</ul>
            					</nav>
            				</div>
            			</div>
            			<div class="row">
            				<div class="col-sm-4 col-xs-12 cei">
            					<a href="https://www.ucm.es/hrs4r" target="_blank" title="UCM - HR Excellence in Research"><img src="/themes/ucm16/media/img/hr.jpg" alt="HR Excellence in Research" /></a>
            				</div>
            				<div class="col-sm-4 col-xs-12 cei">
            					<a href="http://www.campusmoncloa.es/" target="_blank" title="CAMPUS DE EXCELENCIA INTERNACIONAL"><img src="/themes/ucm16/media/img/cei.jpg" alt="CAMPUS DE EXCELENCIA INTERNACIONAL" /></a>
            				</div>
            				<div class="col-sm-4 col-xs-12 cei">
            					<a href="https://www.una-europa.eu/" target="_blank" title="UNA - University Alliance Europe"><img src="/themes/ucm16/media/img/una.jpg" alt="UNA - University Alliance Europe" /></a>
            				</div>
            			</div>
            		</div>
            		<div class="container">
            			<div class="row" id="pie_contacto">
            				<div class="col-sm-3 col-xs-12">&copy; Universidad Complutense Madrid</div>
            				<div class="col-sm-3 col-xs-12"><a href="/contacto" title="Localización y contacto">Localización y contacto</a></div>
            				<div class="col-sm-2 col-xs-12"><a href="/aviso-legal" title="Aviso Legal">Aviso Legal</a></div>
            				<div class="col-sm-3 col-xs-12"><a href="https://www.ucm.es/datos-personales" title="Protección de datos">Protección de datos</a></div>
            				<div class="col-sm-1 col-xs-12"><a href="https://www.ucm.es/rss/rss.php?weid=3" title="RSS">RSS</a></div>
            			</div>
            		</div>		
            	</footer>
            			
            	<script type="text/javascript" src="/themes/ucm16/js/jquery.min.js"></script>
            	<script type="text/javascript" src="/themes/ucm16/js/bootstrap.js"></script>
            	<script type="text/javascript" src="/themes/ucm16/js/ucm.js"></script>
            	<script type="text/javascript">
            	    (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
            	    (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
            	    m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
            	    })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
            	    ga('create', 'UA-299350-23', 'ucm.es');
            	    ga('create', 'UA-299350-1', 'ucm.es', 'ucmHistorico');
            	    ga('send', 'pageview');
            	    ga('ucmHistorico.send', 'pageview');
            	    setTimeout("ga('send','event','NoBounce','page visit over 5 seconds')",5000);
            	</script>
            </body>
            </html>
            """;
}
