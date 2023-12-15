import { z } from "zod";
import states from "@/api/states/states";
import issuingBodies from "@/api/issuingBodies/issuingBodies";

type IValidations = "procuracao" | "nome_completo" | "profissao" 
| "nacionalidade" | "estado_civil" | "uniao_estavel" | "rg" 
| "uf_do_rg" | "nome_da_mae" | "orgao_emissor"| "email" | "cpf" | "cep" 
| "cepNotFound" | "uf_do_endereco" | "cidade" | "bairro"
| "logradouro" | "tipo_de_complemento_do_endereco" | "complemento_do_endereco"
| "ctps_n" | "ctps_serie" | "uf_do_ctps" | "tipo_pessoa"

function getValidation(key:IValidations,isOptional:boolean){
    const validations = {
        procuracao: z.array(z.string()).refine((value) => {
            return (
                value.includes("previdenciario") || 
                value.includes("civel") || 
                value.includes("administrativo") ||
                value.includes("trabalhista")
            );
        },{
            message: "Seleção inválida!"
        }),
        nome_completo: z.string().refine((value)=>{
            if(value.match(/\d/)){
                return false;
            }
            if(value.match(/^[A-Za-z\s'-]+|[\p{L}\s'-]+$/u)){
             return true;   
            }
            return false;
            },{
            message: "Nome e sobrenome é necessário!"
        }),
        profissao: z.string().min(5,{
            message: "Mínimo de 5 caracteres!"
        }),
        nacionalidade: z.string().min(5,{
            message: "Mínimo de 5 caracteres!"
        }),
        estado_civil: z.string().refine((value) => {
            return ["solteiro","casado","divorciado","viúvo"].includes(value);
        },{
            message: "Seleção inválida!"
        }),
        uniao_estavel: z.string().refine((value) => {
            return ["true","false"].includes(value);
        },{
            message: "Seleção inválida!"
        }),
        rg: z.string().regex(/^(\d{1,2}).(\d{3}).(\d{3})-(\d{1})$/,{
            message: "Formato do RG informado está incorreto!"
        }),
        uf_do_rg: z.undefined().or(z.string().regex(/^[A-Z]{2}$/,{
            message: "Estado informado não existe em nosso banco de dados!"
        })),
        orgao_emissor: z.undefined().or(z.string().regex(/^[A-Z]{1,7}$/,{
            message: "Orgão emissor informado não existe em nosso banco de dados!"
        })),
        cpf: z.string().regex(/^(\d{3}).(\d{3}).(\d{3})-(\d{2})$/,{
            message: "Formato do CPF informado está incorreto!"
        }),
        email: z.string().email({
            message: "O formato de E-mail é inválido!"
        }),
        nome_da_mae: z.string().refine((value)=>{
            if(value.match(/\d/)){
                return false;
            }
            if(value.match(/^[A-Za-z\s'-]+|[\p{L}\s'-]+$/u)){
             return true;   
            }
            return false;
            },{
            message: "Nome e sobrenome é necessário!"
        }),
        cep: z.string().regex(/^(\d{5})-(\d{3})$/,{
            message: "Formato do CEP informado está incorreto!"
        }),
        cepNotFound: z.coerce.boolean().refine((value) => {
            return ["true","false"].includes(value.toString());
        },{
            message: "Seleção inválida!"
        }),
        uf_do_endereco: z.string().regex(/^[A-Z]{2}$/,{
            message: "Estado informado não existe em nosso banco de dados!"
        }),
        cidade: z.string().refine((value)=>{
            if(value.match(/\d/)){
                return false;
            }
            if(value.match(/^[A-Za-z\s'-]+|[\p{L}\s'-]+$/u)){
             return true;   
            }
            return false;
            },{
            message: "Cidade não tem um formato válido!"
        }),
        bairro: z.string().refine((value)=>{
            if(value.match(/\d/)){
                return false;
            }
            if(value.match(/^[A-Za-z\s'-]+|[\p{L}\s'-]+$/u)){
             return true;   
            }
            return false;
            },{
            message: "Bairro não tem um formato válido!"
        }),
        logradouro: z.string().refine((value)=>{
            if(value.match(/\d/)){
                return false;
            }
            if(value.match(/^[A-Za-z\s'-]+|[\p{L}\s'-]+$/u)){
             return true;   
            }
            return false;
            },{
            message: "Logradouro não tem um formato válido!"
        }),
        tipo_de_complemento_do_endereco: z.coerce.string().refine((value) => {
            return ["number","qd-lt"].includes(value);
        },{
            message: "Seleção inválida!"
        }),
        complemento_do_endereco: z.string().refine((value) => {
            if(!value.substring(3)?.match(/^[\D]*$/) && value.substring(0,2) == "nº"){
                return true;
            }
            if(value.match(/^Quadra \d+ Lote \d+$/)){
                return true;
            }
            return false;
        },{
            message: "Complemento não tem um formato válido!"
        }),
        ctps_n: z.string().refine((value)=>value.match(/^\d{7}$/),{
            message: "CTPS nº não tem um formato válido!"
        }),
        ctps_serie: z.string().refine((value)=>value.match(/^\d{4}$/),{
            message: "CTPS Serie não tem um formato válido!"
        }),
        uf_do_ctps: z.string().regex(/^[A-Z]{2}$/,{
            message: "Estado informado não existe em nosso banco de dados!"
        }),
        tipo_pessoa: z.string().refine((value)=>{
            return ["pessoa_fisica","pessoa_juridica"].includes(value);
            },{
            message: "Tipo de pessoa é necessário!"
        }),
    }
    if(isOptional){
        return z.undefined().or(validations[key]);
    }else{
        return validations[key];
    }
}

export const signUp = z.object({
    tipo_pessoa: getValidation("tipo_pessoa",false),
    procuracao: getValidation("procuracao",false) as z.ZodEffects<z.ZodArray<z.ZodString, "many">, string[], string[]>,
    nome_completo: getValidation("nome_completo",false),
    profissao: getValidation("profissao",false),
    nacionalidade: getValidation("nacionalidade",false),
    estado_civil: getValidation("estado_civil",false),
    uniao_estavel: getValidation("uniao_estavel",false),
    rg: getValidation("rg",false),
    uf_do_rg: getValidation("uf_do_rg",false),
    orgao_emissor: getValidation("orgao_emissor",false),
    cpf: getValidation("cpf",false),
    email: getValidation("email",false),
    nome_da_mae: getValidation("nome_da_mae",false),
    cep: getValidation("cep",true),
    cepNotFound: getValidation("cepNotFound",true),
    uf_do_endereco: getValidation("uf_do_endereco",true),
    cidade: getValidation("cidade",true),
    bairro: getValidation("bairro",true),
    logradouro: getValidation("logradouro",true),
    tipo_de_complemento_do_endereco: getValidation("tipo_de_complemento_do_endereco",true),
    complemento_do_endereco: getValidation("complemento_do_endereco",true),
    ctps_n: getValidation("ctps_n",true),
    ctps_serie: getValidation("ctps_serie",true),
    uf_do_ctps: getValidation("uf_do_ctps",true),
})