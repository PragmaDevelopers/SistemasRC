import { Document, Page, Text, Image, View, Link,StyleSheet } from "@react-pdf/renderer";
import { IFormSignUpInputs } from "@/Interface/IFormInputs";
import classifiedImage from "../../../../../../public/confidential.png";

const styles = StyleSheet.create({
  page: {
    flexDirection: "column",
    backgroundColor: "#FFFFFF"
  },
  section: {
    margin: 10,
    padding: 10,
    flexGrow: 1
  },
  h1: {
    fontSize: 12,
    textAlign:"center"
  },
  h2: {
    fontSize: 12,
    textAlign:"center",
    marginTop:10
  },
  textRed: {
    color: "red"
  },
  textUppercase: {
    textTransform: "uppercase"
  },
  line: {
    borderTop: 1, 
    borderColor: 'black',
    marginTop: 30,
  },
  p: {
    fontSize: 12,
    marginTop: 10
  },
});

function Trabalhista({data}:{data:IFormSignUpInputs}) {
    return (
        <Document>
            <Page size="A4" style={styles.page}>
                <View style={styles.section}>
                    <Image style={{ width: "100%",position:"absolute",opacity: 0.2 }} src={classifiedImage.src} />

                    <Text style={styles.h1}>CONTRATO DE PRESTAÇÃO DE SERVIÇOS</Text>
                    <Text style={styles.h2}>I – DAS PARTES</Text>

                    <Text style={styles.p}>CONTRATANTE – <Text style={styles.textUppercase}>{data?.full_name}</Text>, {data?.nationality}, {data?.marital_status}, {Boolean(data?.common_law_marriage) && "não"} vive em união estável, {data?.ocuppation}, portador da carteira de identidade nº {data?.rg} {data?.issuing_body}/{data?.uf_for_RG}, inscrito no CPF sob nº {data?.cpf}, filho de {data?.mother_name}, endereço eletrônico: <Link src={data?.email}>{data?.email}</Link> residente e domiciliado à {data?.address_name}, {data?.address_complement_name}, {data?.neighborhood}, {data?.city}, {data?.state_for_address}, CEP: {data?.cep}.</Text>

                    <Text style={styles.p}>CONTRATADO – RAFAEL DO CANTO SOCIEDADE INVIVIDUAL DE ADVOCACIA, pessoa jurídica de direito privado, devidamente inscrita no CNPJ sob n° 35.573.196/0001-98 com sede na Rua Santa Maria,n° 60, Campo Lindo, Seropédica- RJ, CEP: 23.898-111, endereço eletronico <Link src={data?.email}>contato@rafaeldocanto.com.br</Link>, neste ato representado por seu sócio administrador e RAFAEL DO CANTO SILVA, brasileiro, solteiro, não vive em união estável, Advogado, inscrito na OAB/RJ 207.010, CPF: 000.000.000-00, com escritorio profissional situado na Avenida Ministro Fernando Costa, nº 1.119, Loja 10, Centro Comercial de Seropédica, Fazenda Caxias, Seropédica, RJ, CEP: 23.895-265 endereço eletrônico: <Link src={data?.email}>rafael@rafaeldocanto.com.br</Link>, têm entre si, justo e contratado os serviços de advocacia com cláusulas, objeto e condições seguintes.</Text>

                    <Text style={styles.h2}>I - DO OBJETO</Text>

                    <Text style={styles.p}>Cláusula 1° - OS CONTRATADOS representarão o CONTRATANTE, na ação de EXIBIÇÃO/RETIFICAÇÃO de Perfil Profissiográfico previdenciário e LTCAT, em face de empresa cujo CONTRATANTE manteve vinculo empregatício.</Text>

                    <Text style={styles.p}>Parágrafo Primeiro – O presente contrato estende-se para todas as empresas que sejam indispensável o objeto do presente para a intrução do requerimento de sua aposentadoria em face do INSS (Instituto Nacional do Seguro Social).</Text>

                    <Text style={styles.p}>Paragrafo Segundo – Além do objeto previsto na clausula 1 ° do presente contrato, os CONTRATADOS também buscaram reparação por danos morais e/ ou materiais em face das empresas.</Text>

                    <Text style={styles.h2}>II – DAS OBRIGAÇÕES</Text>

                    <Text style={styles.p}>Cláusula 3° - Fica o CONTRANTE obrigado fornecer a documentação necessária ao fiel cumprimento do contrato pelos CONTRATADOS e andamento do serviço objeto do presente contratado, sendo notificado através do aplicativo WhatsApp, e-mail, telefone ou qualquer outro meio de contato informado aos CONTRATADOS.</Text>

                    <Text style={styles.p}>Cláusula 2° - OS CONTRATADOS têm obrigação de dedicar seus melhores esforços na prestação dos serviços contratados. Porém, o CONTRATANTE fica desde já ciente de que a advocacia é uma atividade de meio, e não de resultado, de modo que não é possível garantir o êxito favorável ao CONTRATANTE no final da fase administrativa.</Text>

                    <Text style={styles.p}>Paragrafo Primeiro – o CONTRATANTE terá o prazo de (03) três dias para encaminhar aos CONTRATADOS a informação / documento solicitado legível e no formato em que requerido sob pena de infração contratual, podendo ser rescindido unilateralmente por parte dos CONTRATADOS sem prejuízo do pagamento do valor do contrato pelos CONTRATADOS além da multa correspondente a 50% (cinquenta por cento) do valor do presente contrato.</Text>

                    <Text style={styles.p}>Cláusula 4° - Caso o CONTRATANTE altere qualquer dos meios de contato fornecido aos CONTRATADOS, fica o CONTRATANTE obrigado a informar a respectiva alteração aos CONTRATADOS através do endereço eletrônico <Link src="contato@rafaeldocanto.com.br">contato@rafaeldocanto.com.br</Link>, ou través da ferramenta WhatsApp (21) 9 71876498, sob pena de rescisão por parte do CONTRATANTE, incidindo assim a multa no valor correspondente a 50% (cinquenta por cento) do valor perseguido no presente contrato  isentando os CONTRATADOS de qualquer responsabilidade e quaisquer danos oriundos do abjeto do presente contrato.</Text>

                    <Text style={styles.h2}>III – DA OUTORGA DO VALOR DO CONTRATO E DA RESILIÇÃO</Text>

                    <Text style={styles.p}>Cláusula 5° - Por este instrumento ficam os CONTRATADOS nomeados procuradores com a cláusula ad extra judicia, com a finalidade de promover os atos tendentes a defesa dos interesses do CONTRATANTE sendo este celebrado por prazo indeterminado cujo coincidirá com encerramento de todas as instancias judiciais.</Text>

                    <Text style={styles.p}>Cláusula 6° - Em contraprestação aos serviços juridicos até o despacho decisório o CONTRATANTE pagará aos CONTRATATOS o equivalente a UM SALÁRIO MÍNIMO OU R$ 1320,00 (MIL TREZENTOS E VINTE REAIS) vigente nacional na data do pagamento pelo CONTRATANTE (valor por empresa).</Text>

                    <Text style={styles.p}>Paragrafo Primeiro – Caso haja êxito em raparação finaiceira por danos morais e/ou materiais, além do previsto na clausula 6°, será devido aos CONTRATADOS o correspondente a 30% (trinta por cento) do proveito economico auferido no objeto do presente contrato (valor por empresa).</Text>

                    <Text style={styles.p}>Paragrafo Segundo - O início do prazo prescricional começa a contar da data do arquivamento do feito em que houve a EXIBIÇÃO/RETIFICAÇÃO do/dos referido/dos documento/os, quando judicial. Quando ocorrer de forma espontanea pela empresa o prazo incia-se na data da efetiva entraga da documentação correta.</Text>

                    <Text style={styles.p}>Paragrafo Terceiro – Em caso de insdimplência o valor do presente contrato será atualizado pela taxa SELIC além de juros mora de 1%(um por cento) ao mês até a data do efetivo pagamento pelo CONTRATANTE que com a assinatura do presente contrato autoriza os CONTRATADOS a realizarem a COMPENSAÇÃO do valor devido pelo CONTRATANTE em outro processo em que os CONTRATADOS atuem em favor do CONTRATANTE.</Text>

                    <Text style={styles.p}>Cláusula 7° - Iniciaido os trabalhos caso seja retirada dos CONTRATADOS a chance de proseguir o êxito na causa, seja por revogação de mandato ou rescisão unilateral será devido pelo CONTRATANTE o valor correspondente a 30% (trinta por cento) do valor perseguido pelos CONTRATADOS no feito, além de multa correspondente a 20 (vinte) vezes o valor do salario minimo vigente nacional na data da revogação do mandato ou da rescisão contratual, devendo ser pago em 05(cinco) dias corridos após o fato gerador, sob pena de juros e correção monetária nos termos do Paragrafo Terceiro da Cluausula 6°.</Text>

                    <Text style={styles.p}>Parágrafo Único – Em hipótese alguma os CONTRATADOS se comprometem a informar o prazo de duração do serviço aqui prestado, obrigando-se unicamente a honrar com os prazos legais que lhe couberem nos termos da lei.</Text>

                    <Text style={styles.p}>Clausula 8° - O CONTRATANTE arcará com as despesas com o envio de telegrama de notificação com cópia e aviso de recebimento através da Empresa Brasileira de Correios e Telégrafos (CORREIOS) conforme valor cobrado pelo mesmo. </Text>

                    <Text style={styles.h2}>V - DA LGPD</Text>

                    <Text style={styles.p}>Cláusula 9° - O CONTRATANTE autoriza o tratamento e armazenamento de seus dados digitais pelos CONTRATADOS, tais como documentos, mídias e informações privadas, para o exercício regular de seus direitos no processo judicial ou administrativo, objetos do presente contrato, ficando desde já autorizado o compartilhamento com estagiários, parceiros, associados, profissionais contratados ou que prestem qualquer tipo de serviço aos CONTRATADOS, nos termos dos arts. 7º, VI, da LGPD e, com base no art. 10º, I, da LGPD, ostenta legítimo interesse em armazenar, acessar, avaliar, modificar, transferir e comunicar, sob qualquer forma e por tempo indeterminado, todas e quaisquer peças processuais, contratos, e-mails, cartas e demais documentações relativas ao objeto desta contratação visando à concepção e execução de trabalhos idênticos ou similares aos desta contratação vedado o compartilhamento para qualquer outro fim, salvo com autorização expressa pelo CONTRATANTE.</Text>

                    <Text style={styles.h2}>VI – NATUREZA</Text>

                    <Text style={styles.p}>Cláusula 10° - As partes reconhecem o presente instrumento como título executivo extrajudicial consoante descreve o Código de Processo Civil Brasileiro artigo 585 do CPC, sendo os valores devidamente corrigidos pelo índice IGPM/FGV.</Text>

                    <Text style={styles.h2}>VII – DO FORO COMPETENTE</Text>

                    <Text style={styles.p}>Cláusula 11° - Fica eleito o foro da Comarca de Seropédica-RJ, para dirimir as questões oriundas do presente instrumento, com renúncia a qualquer outro, por mais privilegiado que seja pelo CONTARTANTE.</Text>

                    <Text style={styles.h2}>VIII – DA CIÊNCIA</Text>

                    <Text style={styles.p}>Cláusula 12° - Nos termos do artigo 46 do CDC, declara CONTRATANTE que antes de assinar o contrato, procedeu a leitura dos artigos que o constitui, entendeu o teor e as condições de cada um, inexistindo dúvida, os aceitou inteiramente, e assim firmou o presente contrato em duas vias, aderindo de livre e espontânea iniciativa as condições deste contrato, para que surtam seus efeitos legais, renunciando no sentido de alegar futuramente discordância, ignorância ou indenização.</Text>

                    <Text style={styles.p}>E, por assim se acharem justos e contratados, as partes firmam o presente instrumento, em duas vias de igual teor e forma, obrigando-se por si e sucessores ao fiel cumprimento das cláusulas e condições aqui estipuladas.</Text>

                    <Text style={styles.h2}>Rio de janeiro, <Text style={styles.textRed}>24 de Agosto de 2023.</Text></Text>

                    <View style={{marginTop:20,display:"flex",flexDirection:"row",gap: 10}}>
                      <View style={{width:"50%",textAlign:"center"}}>
                        <Text style={styles.p}>CONTRATANTE</Text>
                        <Text style={styles.line}></Text>
                        <Text style={{...styles.p,...styles.textUppercase,...styles.textRed}}>{data?.full_name}</Text>
                      </View>
                      <View style={{width:"50%",textAlign:"center"}}>
                        <Text style={styles.p}>CONTRATADO</Text>
                        <Text style={styles.line}></Text>
                        <Text style={styles.p}>RAFAEL DO CANTO SILVA</Text>
                      </View>
                    </View>

                    <View style={{textAlign:"center",marginTop:20}}>
                      <Text style={styles.p}>CONTRATADO</Text>
                      <Text style={{...styles.line,width:"50%",marginLeft: "auto",marginRight:"auto"}}></Text>
                      <Text style={styles.p}>RAFAEL DO CANTO SILVA</Text>
                    </View>

                    <View style={{marginTop:20,display:"flex",flexDirection:"row",gap: 10}}>
                      <View style={{width:"50%",textAlign:"center"}}>
                        <Text style={styles.line}></Text>
                        <Text style={styles.p}>TES: ISABELA DE OLIVEIRA SOUZA</Text>
                        <Text style={styles.p}>CPF: 173.941.217-63</Text>
                      </View>
                      <View style={{width:"50%",textAlign:"center"}}>
                        <Text style={styles.line}></Text>
                        <Text style={styles.p}>TES: KARINA DE M. DA SILVA</Text>
                        <Text style={styles.p}>CPF: 124.073.917-64</Text>
                      </View>
                    </View>
                </View>
            </Page>
        </Document>
    ); 
}

export default Trabalhista;