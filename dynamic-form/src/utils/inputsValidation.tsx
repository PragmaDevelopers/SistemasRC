import { z } from "zod";
import states from "../../api/states/states";
import issuingBodies from "../../api/issuingBodies/issuingBodies";

const signUpSchema = z.object({
    power_of_attorney: z.array(z.string()).refine((value) => {
        return (
            value.includes("previdenciario") || 
            value.includes("civel") || 
            value.includes("administrativo") ||
            value.includes("trabalhista")
        );
    },{
        message: "Seleção inválida!"
    }),
    full_name: z.string().refine((value)=>{
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
    ocuppation: z.string().min(5,{
        message: "Mínimo de 5 caracteres!"
    }),
    nationality: z.string().min(5,{
        message: "Mínimo de 5 caracteres!"
    }),
    marital_status: z.string().refine((value) => {
        return ["solteiro","casado","divorciado","viúvo"].includes(value);
    },{
        message: "Seleção inválida!"
    }),
    common_law_marriage: z.string().refine((value) => {
        return ["true","false"].includes(value);
    },{
        message: "Seleção inválida!"
    }),
    rg: z.string().regex(/^(\d{1,2}).(\d{3}).(\d{3})-(\d{1})$/,{
        message: "Formato do RG informado está incorreto!"
    }),
    uf_for_RG: z.undefined().or(z.string().regex(/^[A-Z]{2}$/,{
        message: "Estado informado não existe em nosso banco de dados!"
    })),
    issuing_body: z.undefined().or(z.string().regex(/^[A-Z]{1,7}$/,{
        message: "Orgão emissor informado não existe em nosso banco de dados!"
    })),
    cpf: z.string().regex(/^(\d{3}).(\d{3}).(\d{3})-(\d{2})$/,{
        message: "Formato do CPF informado está incorreto!"
    }),
    email: z.string().email({
        message: "O formato de E-mail é inválido!"
    }),
    mother_name: z.string().refine((value)=>{
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
    cep: z.undefined().or(z.string().regex(/^(\d{5})-(\d{3})$/,{
        message: "Formato do CEP informado está incorreto!"
    })),
    cepNotFound: z.undefined().or(z.coerce.boolean().refine((value) => {
        return ["true","false"].includes(value.toString());
    },{
        message: "Seleção inválida!"
    })),
    state_for_address: z.undefined().or(z.string().regex(/^[A-Z]{2}$/,{
        message: "Estado informado não existe em nosso banco de dados!"
    })),
    city: z.undefined().or(z.string().refine((value)=>{
        if(value.match(/\d/)){
            return false;
        }
        if(value.match(/^[A-Za-z\s'-]+|[\p{L}\s'-]+$/u)){
         return true;   
        }
        return false;
        },{
        message: "Cidade não tem um formato válido!"
    })),
    neighborhood: z.undefined().or(z.string().refine((value)=>{
        if(value.match(/\d/)){
            return false;
        }
        if(value.match(/^[A-Za-z\s'-]+|[\p{L}\s'-]+$/u)){
         return true;   
        }
        return false;
        },{
        message: "Bairro não tem um formato válido!"
    })),
    // // address_type_id: number,
    address_name: z.undefined().or(z.string().refine((value)=>{
        if(value.match(/\d/)){
            return false;
        }
        if(value.match(/^[A-Za-z\s'-]+|[\p{L}\s'-]+$/u)){
         return true;   
        }
        return false;
        },{
        message: "Logradouro não tem um formato válido!"
    })),
    address_complement_type: z.undefined().or(z.coerce.string().refine((value) => {
        return ["number","qd-lt"].includes(value);
    },{
        message: "Seleção inválida!"
    })),
    address_complement_name: z.undefined().or(z.string().refine((value) => {
        if(!value.substring(3)?.match(/^[\D]*$/) && value.substring(0,2) == "nº"){
            return true;
        }
        if(value.match(/^Quadra \d+ Lote \d+$/)){
            return true;
        }
        return false;
    },{
        message: "Complemento não tem um formato válido!"
    })),
    ctps_n: z.undefined().or(z.string().refine((value)=>value.match(/^\d{7}$/),{
        message: "CTPS nº não tem um formato válido!"
    })),
    ctps_serie: z.undefined().or(z.string().refine((value)=>value.match(/^\d{4}$/),{
        message: "CTPS Serie não tem um formato válido!"
    })),
    uf_for_ctps_id: z.undefined().or(z.coerce.number().refine((value) => {
        return states.some((state)=>state.id == value);
    },{
        message: "Estado informado não existe em nosso banco de dados!"
    }))
})

export default signUpSchema;