"use client"
import React, { ReactNode, useState, useEffect } from "react";
import { IFormSignUpAInputs } from "../../../Interface/IFormInputs";
import ICepApi from "../../../Interface/ICepApi";
import { useForm, SubmitHandler } from "react-hook-form";
import { useRouter } from "next/navigation";
import {
  PowerOfAttorney,
  Ocuppation,
  Nationality,
  MaritalStatus,
  UfForRG,
  IssuingBody,
  StateForAddress,
  City,
  Neighborhood,
  AddressName,
  UfForCTPS
} from "@/components/form/InputSelect/InputSelect";
import { FullName, Email, RG,CPF,MotherName,CEP,CTPSn,CTPSserie } from "@/components/form/InputText/InputText";
import { CommonLawMarriage,AddressComplement } from "@/components/form/InputRadio/InputRadio";
import { getAddressManually, tryGetAddressByCep } from "@/utils/handleError";
import {signUpA} from "@/utils/inputsValidation";
import { zodResolver } from "@hookform/resolvers/zod";
import { AccordionItem } from "@/components/AccordionItem";

export default function SignUpPage() {
  const { register, handleSubmit, watch,setValue,formState: {errors} } = useForm<IFormSignUpAInputs>({
    defaultValues: {
      procuracao: [],
      cepNotFound: false,
    },
    resolver: zodResolver(signUpA)
  });

  const [cepData,setCepData] = useState<ICepApi>({
    uf: [""],
    localidade: [""],
    bairro: [""],
    logradouro: [""]
  });

  const router = useRouter();
  const onSubmit: SubmitHandler<IFormSignUpAInputs> = (data) => {
    if(Boolean(data.uniao_estavel)){
      data.uniao_estavel = "vive em união estável"
    }else{
      data.uniao_estavel = "não vive em união estável"
    }
    sessionStorage.setItem("registration_form",JSON.stringify(data));
    router.push("/pdf_page/edit")
  };
  
  useEffect(()=>{
    async function awaitFunction(){
       const response = await tryGetAddressByCep(watch);
       setCepData(response);
    }
    awaitFunction();
  },[watch().cep])

  useEffect(()=>{
    //QUANDO TROCAR O TIPO DE PROCURAÇÃO LIMPAR OS CAMPOS NECESSÁRIOS
    if(!watch().procuracao?.includes("trabalhista")){
      setValue("ctps_n",undefined)
      setValue("ctps_serie",undefined)
      setValue("uf_do_ctps",undefined)
    }
    if(!watch().procuracao?.includes("previdenciario") && 
      !watch().procuracao?.includes("civel") && 
      !watch().procuracao?.includes("administrativo")){
        setValue("cep",undefined)
        setValue("cepNotFound",false)
        setValue("uf_do_endereco",undefined)
        setValue("cidade",undefined)
        setValue("bairro",undefined)
        setValue("logradouro",undefined)
      if(!watch().procuracao?.includes("administrativo")){
        setValue("tipo_de_complemento_do_endereco",undefined)
        setValue("complemento_do_endereco",undefined)
      }
    }
  },[watch().procuracao])

  return (
    <form className="mx-auto w-full max-w-lg my-3 px-2" onSubmit={handleSubmit(onSubmit)}>
      <div className="bg-gray-200 p-5 border-b-2 border-gray-400">
        <AccordionItem className="" title="Campos gerais" isOpen={true}>
          <PowerOfAttorney className={"mb-3"} register={register} watch={watch} setValue={setValue} />
          {errors.procuracao && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.procuracao.message}</p>}
          <FullName className={"mb-3"} register={register} />
          {errors.nome_completo && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.nome_completo.message}</p>}
          <Ocuppation className={"mb-3"} register={register} />
          {errors.profissao && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.profissao.message}</p>}
          <Nationality className={"mb-3"} register={register} />
          {errors.nacionalidade && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.nacionalidade.message}</p>}
          <Email className={""} register={register} />
          {errors.email && <p className="text-red-500 border-b-2 border-red-500">{errors.email.message}</p>}
        </AccordionItem>
      </div>
      <div className="bg-gray-200 p-5 border-b-2 border-gray-400">
        <AccordionItem className="" title="Campos de documentos">
          <MaritalStatus className={"mb-3"} register={register} />
          {errors.estado_civil && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.estado_civil.message}</p>}
          <CommonLawMarriage className={"mb-3"} register={register} />
          {errors.uniao_estavel && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.uniao_estavel.message}</p>}
          <RG className={"mb-3"} register={register} setValue={setValue} />
          {errors.rg && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.rg.message}</p>}
          <UfForRG className={"mb-3"} register={register} />
          {errors.uf_do_rg && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.uf_do_rg.message}</p>}
          <IssuingBody className={"mb-3"} register={register} />
          {errors.orgao_emissor && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.orgao_emissor.message}</p>}
          <CPF className={"mb-3"} register={register} setValue={setValue} />
          {errors.cpf && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.cpf.message}</p>}
          <MotherName className={""} register={register} />
          {errors.nome_da_mae && <p className="text-red-500 my-3 border-b-2 border-red-500">{errors.nome_da_mae.message}</p>}
        </AccordionItem>
      </div>
      {
          (
            watch().procuracao?.includes("previdenciario") || 
            watch().procuracao?.includes("civel") || 
            watch().procuracao?.includes("administrativo")
          ) && (
          <div className="bg-gray-200 p-5 border-b-2 border-gray-400">
            <AccordionItem className="" title="Campos extras">
              <CEP className={"mb-3"} register={register} setValue={setValue} />
              {errors.cep && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.cep.message}</p>}
              <StateForAddress className={"mb-3"} register={register} watch={watch} setValue={setValue} apiInfo={cepData.uf} />
              {errors.uf_do_endereco && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.uf_do_endereco.message}</p>}
              <City className={"mb-3"} register={register} watch={watch} setValue={setValue} apiInfo={cepData.localidade} />
              {errors.cidade && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.cidade.message}</p>}
              <Neighborhood className={"mb-3"} register={register} watch={watch} setValue={setValue} apiInfo={cepData.bairro} />
              {errors.bairro && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.bairro.message}</p>}
              {/* <AddressType className={"mb-3"} register={register} watch={watch} /> */}
              {
                watch().cepNotFound && 
                  (
                    <div className="border-b-2 border-gray-400 mb-3">
                      <button className="bg-white py-2 px-3 mb-3" type="button" onClick={async()=>{setCepData(await getAddressManually(watch))}}
                      >Buscar Endereço</button>
                    </div>
                  )
              }
              <AddressName className={""} register={register} watch={watch} setValue={setValue} apiInfo={cepData.logradouro} />
              {errors.logradouro && <p className="text-red-500 mt-3 border-b-2 border-red-500">{errors.logradouro.message}</p>}
              {
                (
                  watch().procuracao?.includes("previdenciario") || 
                  watch().procuracao?.includes("civel")
                ) && (
                <>
                  <AddressComplement className={"mt-3"} register={register} watch={watch} setValue={setValue} />
                  {errors.tipo_de_complemento_do_endereco && <p className="text-red-500 mt-3 border-b-2 border-red-500">{errors.tipo_de_complemento_do_endereco.message}</p>}
                  {errors.complemento_do_endereco && <p className="text-red-500 mt-3 border-b-2 border-red-500">{errors.complemento_do_endereco.message}</p>}
                </>
              )}
            </AccordionItem>
          </div>
        )
      }
      {
          watch().procuracao?.includes("trabalhista") && (
            <div className="bg-gray-200 p-5 border-b-2 border-gray-400">
                <AccordionItem className="" title="Campos extras">
                  <CTPSn className={"mb-3"} register={register} setValue={setValue} />
                  {errors.ctps_n && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.ctps_n.message}</p>}
                  <CTPSserie className={"mb-3"} register={register} setValue={setValue} />
                  {errors.ctps_serie && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.ctps_serie.message}</p>}
                  <UfForCTPS className={""} register={register} />
                  {errors.uf_do_ctps && <p className="text-red-500 mt-3 border-b-2 border-red-500">{errors.uf_do_ctps.message}</p>}
                </AccordionItem>
            </div>
          )
      }
      <div className="bg-gray-200 p-5 border-b-2 border-gray-400">
        <button className="bg-white py-2 px-3" type="submit">Enviar</button>
      </div>
    </form>
  );
}
