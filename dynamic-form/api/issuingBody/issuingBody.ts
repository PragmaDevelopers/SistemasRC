const issuingBody = [
    {
      abbreviation: 'ABNC',
      name: 'Academia Brasileira de Neurocirurgia',
    },
    {
      abbreviation: 'AGU',
      name: 'Advocacia-Geral da União',
    },
    {
      abbreviation: 'ANAC',
      name: 'Agência Nacional de Aviação Civil',
    },
    {
      abbreviation: 'CAER',
      name: 'Clube de Aeronáutica',
    },
    {
      abbreviation: 'CAU',
      name: 'Conselho de Arquitetura e Urbanismo',
    },
    {
      abbreviation: 'CBM',
      name: 'Corpo de Bombeiro Militar',
    },
    {
      abbreviation: 'CFA',
      name: 'Conselho Federal Administração',
    },
    {
      abbreviation: 'CFB',
      name: 'Conselho Federal de Biblioteconomia',
    },
    {
      abbreviation: 'CFBIO',
      name: 'Conselho Federal de Biologia',
    },
    {
      abbreviation: 'CFBM',
      name: 'Conselho Federal de Biomedicina',
    },
    {
      abbreviation: 'CFC',
      name: 'Conselho Federal de Contabilidade',
    },
    {
      abbreviation: 'CFESS',
      name: 'Conselho Federal de Serviço Social',
    },
    {
      abbreviation: 'CFF',
      name: 'Conselho Regional de Farmácia',
    },
    {
      abbreviation: 'CFFA',
      name: 'Conselho Federal de Fonoaudiologia',
    },
    {
      abbreviation: 'CFM',
      name: 'Conselho Federal de Medicina',
    },
    {
      abbreviation: 'CFMV',
      name: 'Conselho Federal de Medicina Veterinária',
    },
    {
      abbreviation: 'CFN',
      name: 'Conselho Federal de Nutrição',
    },
    {
      abbreviation: 'CFO',
      name: 'Conselho Federal de Odontologia',
    },
    {
      abbreviation: 'CFP',
      name: 'Conselho Federal de Psicologia',
    },
    {
      abbreviation: 'CFQ',
      name: 'Conselho Regional de Química',
    },
    {
      abbreviation: 'CFT',
      name: 'Conselho Federal dos Técnicos Industriais',
    },
    {
      abbreviation: 'CFTA',
      name: 'Conselho Federal dos Técnicos Agrícolas',
    },
    {
      abbreviation: 'CGPI',
      name: 'Coordenação Geral de Privilégios e Imunidades',
    },
    {
      abbreviation: 'CGPMAF',
      name:
        'Coordenadoria Geral de Polícia Marítima, Aeronáutica e de Fronteiras',
    },
    {
      abbreviation: 'CIPC',
      name: 'Centro de Inteligência da Polícia Civil',
    },
    {
      abbreviation: 'CNIG',
      name: 'Conselho Nacional de Imigração',
    },
    {
      abbreviation: 'CNT',
      name: 'Confederação Nacional do Transporte',
    },
    {
      abbreviation: 'CNTV',
      name: 'Confederação Nacional de Vigilantes & Prestadores de Serviços',
    },
    {
      abbreviation: 'COFECI',
      name: 'Conselho Federal de Corretores de Imóveis',
    },
    {
      abbreviation: 'COFECON',
      name: 'Conselho Federal de Economia',
    },
    {
      abbreviation: 'COFEM',
      name: 'Conselho Federal de Museologia',
    },
    {
      abbreviation: 'COFEN',
      name: 'Conselho Federal de Enfermagem',
    },
    {
      abbreviation: 'COFFITO',
      name: 'Conselho Regional de Fisioterapia e Terapia Ocupacional',
    },
    {
      abbreviation: 'COMAER',
      name: 'Comando da Aeronáutica',
    },
    {
      abbreviation: 'CONFE',
      name: 'Conselho Federal de Estatística',
    },
    {
      abbreviation: 'CONFEA',
      name: 'Conselho Federal de Engenharia e Agronomia',
    },
    {
      abbreviation: 'CONFEF',
      name: 'Conselho Federal de Educação Física',
    },
    {
      abbreviation: 'CONFERE',
      name: 'Conselho Federal dos Representantes Comerciais',
    },
    {
      abbreviation: 'CONRE',
      name: 'Conselho Regional de Estatística',
    },
    {
      abbreviation: 'CONRERP',
      name: 'Conselho Federal de Profissionais de Relações Públicas',
    },
    {
      abbreviation: 'CORE',
      name: 'Conselho Regional dos Representantes Comerciais',
    },
    {
      abbreviation: 'CORECON',
      name: 'Conselho Regional de Economia',
    },
    {
      abbreviation: 'COREM',
      name: 'Conselho Regional de Museologia',
    },
    {
      abbreviation: 'COREN',
      name: 'Conselho Regional de Enfermagem',
    },
    {
      abbreviation: 'CRA',
      name: 'Conselho Regional de Administração',
    },
    {
      abbreviation: 'CRAS',
      name: 'Centro de Referência de Assistência Social',
    },
    {
      abbreviation: 'CRB',
      name: 'Conselho Regional de Biblioteconomia',
    },
    {
      abbreviation: 'CRBIO',
      name: 'Conselho Regional de Biologia',
    },
    {
      abbreviation: 'CRBM',
      name: 'Conselho Regional de Biomedicina',
    },
    {
      abbreviation: 'CRC',
      name: 'Conselho Regional de Contabilidade',
    },
    {
      abbreviation: 'CREA',
      name: 'Conselho Regional de Engenharia e Agronomia',
    },
    {
      abbreviation: 'CRECI',
      name: 'Conselho Regional de Corretores de Imóveis',
    },
    {
      abbreviation: 'CREF',
      name: 'Conselho Regional de Educação Física',
    },
    {
      abbreviation: 'CREFITO',
      name: 'Conselho Regional de Fisioterapia e Terapia Ocupacional',
    },
    {
      abbreviation: 'CRESS',
      name: 'Conselho Regional de Serviço Social',
    },
    {
      abbreviation: 'CRF',
      name: 'Conselho Regional de Farmácia',
    },
    {
      abbreviation: 'CRFA',
      name: 'Conselho Regional de Fonoaudiologia',
    },
    {
      abbreviation: 'CRM',
      name: 'Conselho Regional de Medicina',
    },
    {
      abbreviation: 'CRMV',
      name: 'Conselho Regional de Medicina Veterinária',
    },
    {
      abbreviation: 'CRN',
      name: 'Conselho Regional de Nutrição',
    },
    {
      abbreviation: 'CRO',
      name: 'Conselho Regional de Odontologia',
    },
    {
      abbreviation: 'CRP',
      name: 'Conselho Regional de Psicologia',
    },
    {
      abbreviation: 'CRPRE',
      name: 'Conselho Regional de Profissionais de Relações Públicas',
    },
    {
      abbreviation: 'CRQ',
      name: 'Conselho Regional de Química',
    },
    {
      abbreviation: 'CRT',
      name: 'Conselho Regional dos Técnicos Industriais',
    },
    {
      abbreviation: 'CRTA',
      name: 'Conselho Regional de Técnicos de Administração',
    },
    {
      abbreviation: 'CTPS',
      name: 'Carteira de Trabalho e Previdência Social',
    },
    {
      abbreviation: 'CV',
      name: 'Cartório Civil',
    },
    {
      abbreviation: 'DELEMIG',
      name: 'Delegacia de Polícia de Imigração',
    },
    {
      abbreviation: 'DETRAN',
      name: 'Departamento Estadual de Trânsito',
    },
    {
      abbreviation: 'DGPC',
      name: 'Diretoria Geral da Polícia Civil',
    },
    {
      abbreviation: 'DIC',
      name: 'Diretoria de Identificação Civil',
    },
    {
      abbreviation: 'DICC',
      name: 'Diretoria de Identificação Civil e Criminal',
    },
    {
      abbreviation: 'DIREX',
      name: 'Diretoria Executiva',
    },
    {
      abbreviation: 'DPF',
      name: 'Departamento de Polícia Federal',
    },
    {
      abbreviation: 'DPMAF',
      name: 'Divisão de Polícia Marítima, Aérea e de Fronteiras',
    },
    {
      abbreviation: 'DPT',
      name: 'Departamento de Polícia Técnica Geral',
    },
    {
      abbreviation: 'DPTC',
      name: 'Departamento de Polícia Técnico Científica',
    },
    {
      abbreviation: 'DREX',
      name: 'Delegacia Regional Executiva',
    },
    {
      abbreviation: 'DRT',
      name: 'Delegacia Regional do Trabalho',
    },
    {
      abbreviation: 'EB',
      name: 'Exército Brasileiro',
    },
    {
      abbreviation: 'FAB',
      name: 'Força Aérea Brasileira',
    },
    {
      abbreviation: 'FENAJ',
      name: 'Federação Nacional dos Jornalistas',
    },
    {
      abbreviation: 'FGTS',
      name: 'Fundo de Garantia do Tempo de Serviço',
    },
    {
      abbreviation: 'FIPE',
      name: 'Fundação Instituto de Pesquisas Econômicas',
    },
    {
      abbreviation: 'FLS',
      name: 'Fundação Lyndolpho Silva',
    },
    {
      abbreviation: 'FUNAI',
      name: 'Fundação Nacional do Índio',
    },
    {
      abbreviation: 'GEJSP',
      name: 'Gerência de Estado de Justiça, Segurança Pública e Cidadania',
    },
    {
      abbreviation: 'GEJSPC',
      name: 'Gerência de Estado de Justiça, Segurança Pública e Cidadania',
    },
    {
      abbreviation: 'GEJUSPC',
      name: 'Gerência de Estado de Justiça, Segurança Pública e Cidadania',
    },
    {
      abbreviation: 'GESP',
      name: 'Gerência de Estado de Segurança Pública',
    },
    {
      abbreviation: 'GOVGO',
      name: 'Governo do Estado de Goiás',
    },
    {
      abbreviation: 'I CLA',
      name: 'Carteira de Identidade Classista',
    },
    {
      abbreviation: 'ICP',
      name: 'Instituto de Polícia Científica',
    },
    {
      abbreviation: 'IDAMP',
      name: 'Instituto de Identificação Dr. Aroldo Mendes Paiva',
    },
    {
      abbreviation: 'IFP',
      name: 'Instituto Félix Pacheco',
    },
    {
      abbreviation: 'IGP',
      name: 'Instituto Geral de Perícias',
    },
    {
      abbreviation: 'IIACM',
      name: 'Instituto de Identificação Aderson Conceição de Melo',
    },
    {
      abbreviation: 'IICC',
      name: 'Instituto de Identificação Civil e Criminal',
    },
    {
      abbreviation: 'IICCECF',
      name:
        'Instituto de Identificação Civil e Criminal Engrácia da Costa Francisco',
    },
    {
      abbreviation: 'IICM',
      name: 'Instituto de Identificação Carlos Menezes',
    },
    {
      abbreviation: 'IIGP',
      name: 'Instituto de Identificação Gonçalo Pereira',
    },
    {
      abbreviation: 'IIJDM',
      name: 'Instituto de Identificação João de Deus Martins',
    },
    {
      abbreviation: 'IIPC',
      name: 'Instituto de Identificação da Polícia Civil',
    },
    {
      abbreviation: 'IIPC',
      name: 'Instituto de Identificação Pedro Mello',
    },
    {
      abbreviation: 'IIRGD',
      name: 'Instituto de Identificação Ricardo Gumbleton Daunt',
    },
    {
      abbreviation: 'IIRHM',
      name: 'Instituto de Identificação Raimundo Hermínio de Melo',
    },
    {
      abbreviation: 'IITB',
      name: 'Instituto de Identificação Tavares Buril',
    },
    {
      abbreviation: 'IML',
      name: 'Instituto Médico-Legal',
    },
    {
      abbreviation: 'INI',
      name: 'Instituto Nacional de Identificação',
    },
    {
      abbreviation: 'IPF',
      name: 'Instituto Pereira Faustino',
    },
    {
      abbreviation: 'ITCP',
      name: 'Instituto Técnico-Científico de Perícia',
    },
    {
      abbreviation: 'ITEP',
      name: 'Instituto Técnico-Científico de Perícia',
    },
    {
      abbreviation: 'MAER',
      name: 'Ministério da Aeronáutica',
    },
    {
      abbreviation: 'MB',
      name: 'Marinha do Brasil',
    },
    {
      abbreviation: 'MD',
      name: 'Ministério da Defesa',
    },
    {
      abbreviation: 'MDS',
      name: 'Ministério da Cidadania',
    },
    {
      abbreviation: 'MEC',
      name: 'Ministério da Educação e Cultura',
    },
    {
      abbreviation: 'MEX',
      name: 'Ministério do Exército',
    },
    {
      abbreviation: 'MINDEF',
      name: 'Ministério da Defesa',
    },
    {
      abbreviation: 'MJ',
      name: 'Ministério da Justiça',
    },
    {
      abbreviation: 'MM',
      name: 'Ministério da Marinha',
    },
    {
      abbreviation: 'MMA',
      name: 'Ministério da Marinha',
    },
    {
      abbreviation: 'MPAS',
      name: 'Ministério da Previdência e Assistência Social',
    },
    {
      abbreviation: 'MPE',
      name: 'Ministério Público Estadual',
    },
    {
      abbreviation: 'MPF',
      name: 'Ministério Público Federal',
    },
    {
      abbreviation: 'MPT',
      name: 'Ministério Público do Trabalho',
    },
    {
      abbreviation: 'MRE',
      name: 'Ministério das Relações Exteriores',
    },
    {
      abbreviation: 'MT',
      name: 'Ministério do Trabalho',
    },
    {
      abbreviation: 'MTE',
      name: 'Ministério da Economia',
    },
    {
      abbreviation: 'MTPS',
      name: 'Ministério do Trabalho e Previdência Social',
    },
    {
      abbreviation: 'NUMIG',
      name: 'Núcleo de Polícia de Imigração',
    },
    {
      abbreviation: 'OAB',
      name: 'Ordem dos Advogados do Brasil',
    },
    {
      abbreviation: 'OMB',
      name: 'Ordens dos Músicos do Brasil',
    },
    {
      abbreviation: 'PC',
      name: 'Polícia Civil',
    },
    {
      abbreviation: 'PF',
      name: 'Polícia Federal',
    },
    {
      abbreviation: 'PGFN',
      name: 'Procuradoria Geral da Fazenda Nacional',
    },
    {
      abbreviation: 'PM',
      name: 'Polícia Militar',
    },
    {
      abbreviation: 'POLITEC',
      name: 'Perícia Oficial e Identificação Técnica',
    },
    {
      abbreviation: 'PRF',
      name: 'Polícia Rodoviária Federal',
    },
    {
      abbreviation: 'PTC',
      name: 'Polícia Tecnico-Científica',
    },
    {
      abbreviation: 'SCC',
      name: 'Secretaria de Estado da Casa Civil',
    },
    {
      abbreviation: 'SCJDS',
      name: 'Secretaria Coordenadora de Justiça e Defesa Social',
    },
    {
      abbreviation: 'SDS',
      name: 'Secretaria de Defesa Social',
    },
    {
      abbreviation: 'SECC',
      name: 'Secretaria de Estado da Casa Civil',
    },
    {
      abbreviation: 'SECCDE',
      name: 'Secretaria de Estado da Casa Civil e Desenvolvimento Econômico',
    },
    {
      abbreviation: 'SEDS',
      name: 'Secretaria de Estado da Defesa Social',
    },
    {
      abbreviation: 'SEGUP',
      name: 'Secretaria de Estado da Segurança Pública e da Defesa Social',
    },
    {
      abbreviation: 'SEJSP',
      name: 'Secretaria de Estado de Justiça e Segurança Pública',
    },
    {
      abbreviation: 'SEJUC',
      name: 'Secretaria de Estado da Justica',
    },
    {
      abbreviation: 'SEJUSP',
      name: 'Secretaria de Estado de Justiça e Segurança Pública',
    },
    {
      abbreviation: 'SEPC',
      name: 'Secretaria de Estado da Polícia Civil',
    },
    {
      abbreviation: 'SES',
      name: 'Secretaria de Estado da Segurança',
    },
    {
      abbreviation: 'SESC',
      name: 'Secretaria de Estado da Segurança e Cidadania',
    },
    {
      abbreviation: 'SESDC',
      name: 'Secretaria de Estado da Segurança, Defesa e Cidadania',
    },
    {
      abbreviation: 'SESDEC',
      name: 'Secretaria de Estado da Segurança, Defesa e Cidadania',
    },
    {
      abbreviation: 'SESEG',
      name: 'Secretaria Estadual de Segurança',
    },
    {
      abbreviation: 'SESP',
      name: 'Secretaria de Estado da Segurança Pública',
    },
    {
      abbreviation: 'SESPAP',
      name:
        'Secretaria de Estado da Segurança Pública e Administração Penitenciária',
    },
    {
      abbreviation: 'SESPDC',
      name: 'Secretaria de Estado de Segurança Publica e Defesa do Cidadão',
    },
    {
      abbreviation: 'SESPDS',
      name: 'Secretaria de Estado de Segurança Pública e Defesa Social',
    },
    {
      abbreviation: 'SGPC',
      name: 'Superintendência Geral de Polícia Civil',
    },
    {
      abbreviation: 'SGPJ',
      name: 'Superintendência Geral de Polícia Judiciária',
    },
    {
      abbreviation: 'SIM',
      name: 'Serviço de Identificação da Marinha',
    },
    {
      abbreviation: 'SJ',
      name: 'Secretaria da Justiça',
    },
    {
      abbreviation: 'SJCDH',
      name: 'Secretaria da Justiça e dos Direitos Humanos',
    },
    {
      abbreviation: 'SJDS',
      name: 'Secretaria Coordenadora de Justiça e Defesa Social',
    },
    {
      abbreviation: 'SJS',
      name: 'Secretaria da Justiça e Segurança',
    },
    {
      abbreviation: 'SJTC',
      name: 'Secretaria da Justiça do Trabalho e Cidadania',
    },
    {
      abbreviation: 'SJTS',
      name: 'Secretaria da Justiça do Trabalho e Segurança',
    },
    {
      abbreviation: 'SNJ',
      name: 'Secretaria Nacional de Justiça / Departamento de Estrangeiros',
    },
    {
      abbreviation: 'SPMAF',
      name: 'Serviço de Polícia Marítima, Aérea e de Fronteiras',
    },
    {
      abbreviation: 'SPTC',
      name: 'Secretaria de Polícia Técnico-Científica',
    },
    {
      abbreviation: 'SRDPF',
      name: 'Superintendência Regional do Departamento de Polícia Federal',
    },
    {
      abbreviation: 'SRF',
      name: 'Receita Federal',
    },
    {
      abbreviation: 'SRTE',
      name: 'Superintendência Regional do Trabalho',
    },
    {
      abbreviation: 'SSDC',
      name: 'Secretaria da Segurança, Defesa e Cidadania',
    },
    {
      abbreviation: 'SSDS',
      name: 'Secretaria da Segurança e da Defesa Social',
    },
    {
      abbreviation: 'SSI',
      name: 'Secretaria de Segurança e Informações',
    },
    {
      abbreviation: 'SSP',
      name: 'Secretaria de Segurança Pública',
    },
    {
      abbreviation: 'SSPCGP',
      name:
        'Secretaria de Segurança Pública e Coordenadoria Geral de Perícias',
    },
    {
      abbreviation: 'SSPDC',
      name: 'Secretaria de Segurança Pública e Defesa do Cidadão',
    },
    {
      abbreviation: 'SSPDS',
      name: 'Secretaria de Segurança Pública e Defesa Social',
    },
    {
      abbreviation: 'SSPPC',
      name: 'Secretaria de Segurança Pública Polícia Civil',
    },
    {
      abbreviation: 'SUSEP',
      name: 'Superintendência de Seguros Privados',
    },
    {
      abbreviation: 'SUSEPE',
      name: 'Superintendência dos Serviços Penitenciários',
    },
    {
      abbreviation: 'TJ',
      name: 'Tribunal de Justiça',
    },
    {
      abbreviation: 'TJAEM',
      name: 'Tribunal Arbitral e Mediação dos Estados Brasileiros',
    },
    {
      abbreviation: 'TRE',
      name: 'Tribunal Regional Eleitoral',
    },
    {
      abbreviation: 'TRF',
      name: 'Tribunal Regional Federal',
    },
    {
      abbreviation: 'TSE',
      name: 'Tribunal Superior Eleitoral',
    },
    {
      abbreviation: 'XXX',
      name: 'Orgão Estrangeiro',
    },
    {
      abbreviation: 'ZZZ',
      name: 'Outro',
    },
  ];

  export default issuingBody;