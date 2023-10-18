"use client";
import React, { ReactNode, useState, useEffect } from "react";
import InputsInterface from "./Interface/InputsInterface";
import CepDataInterface from "./Interface/CepData";
import { useForm, SubmitHandler } from "react-hook-form";
import {
  PowerOfAttorney,
  Ocuppation,
  Nationality,
  MaritalStatus,
  UfForRG,
  IssuingBody,
  StateForAddress,
  City,
  // AddressType,
  Neighborhood,
  AddressName,
  UfForCTPS
} from "./InputSelect/InputSelect";
import { FullName, Email, RG,CPF,MotherName,CEP,CTPSn,CTPSserie } from "./InputText/InputText";
import { CommonLawMarriage,AddressComplement } from "./InputRadio/InputRadio";
import { getAddressManually, tryGetAddressByCep } from "@/utils/handleError";
import signUpSchema from "@/utils/inputsValidation";
import { zodResolver } from "@hookform/resolvers/zod";

interface AccordionItemProps {
  title: string;
  children: ReactNode;
  isOpen?: boolean; // Adicione a propriedade isOpen para controlar o estado inicial do accordion
}

const AccordionItem: React.FC<AccordionItemProps> = ({ title, children, isOpen = true }) => {
  const [isAccordionOpen, setIsAccordionOpen] = useState(isOpen);

  return (
    <div>
      <button className="text-xl font-bold mb-3" type="button" onClick={() => setIsAccordionOpen(!isAccordionOpen)}>{title}</button>
      {isAccordionOpen && <div>{children}</div>}
    </div>
  );
};

export default function UserForm() {
  const { register, handleSubmit, watch,setValue,formState: {errors} } = useForm<InputsInterface>({
    defaultValues: {
      cepNotFound: false,
    },
    resolver: zodResolver(signUpSchema)
  });

  const onSubmit: SubmitHandler<InputsInterface> = (data) => console.log(data);
  
  useEffect(()=>{
    async function awaitFunction(){
       const response = await tryGetAddressByCep(watch);
       setCepData(response);
    }
    awaitFunction();
  },[watch().cep])

  useEffect(()=>{
    //QUANDO TROCAR O TIPO DE PROCURAÇÃO LIMPAR OS CAMPOS NECESSÁRIOS
    if(["previdenciario","civel","administrativo"].includes(watch().power_of_attorney)){
      setValue("ctps_n",undefined)
      setValue("ctps_serie",undefined)
      setValue("uf_for_ctps_id",undefined)
      if(["administrativo"].includes(watch().power_of_attorney)){
        setValue("address_complement_type",undefined)
        setValue("address_complement_name",undefined)
      }
    }
    if(["trabalhista"].includes(watch().power_of_attorney)){
      setValue("cep",undefined)
      setValue("cepNotFound",false)
      setValue("state_for_address",undefined)
      setValue("city",undefined)
      setValue("neighborhood",undefined)
      setValue("address_name",undefined)
      setValue("address_complement_type",undefined)
      setValue("address_complement_name",undefined)
    }
    console.log(watch())
  },[watch().power_of_attorney])

  const [cepData,setCepData] = useState<CepDataInterface>({
    uf: [""],
    localidade: [""],
    bairro: [""],
    logradouro: [""]
  });

  return (
    <form className="bg-white mx-auto w-full max-w-lg my-3" onSubmit={handleSubmit(onSubmit)}>
      <div className="bg-gray-200 p-5 border-b-2 border-gray-400">
        <AccordionItem title="Campos gerais" isOpen={true}>
          <PowerOfAttorney className={"mb-3"} register={register} />
          {errors.power_of_attorney && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.power_of_attorney.message}</p>}
          <FullName className={"mb-3"} register={register} />
          {errors.full_name && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.full_name.message}</p>}
          <Ocuppation className={"mb-3"} register={register} />
          {errors.ocuppation && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.ocuppation.message}</p>}
          <Nationality className={"mb-3"} register={register} />
          {errors.nationality && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.nationality.message}</p>}
          <Email className={""} register={register} />
          {errors.email && <p className="text-red-500 border-b-2 border-red-500">{errors.email.message}</p>}
        </AccordionItem>
      </div>
      <div className="bg-gray-200 p-5 border-b-2 border-gray-400">
        <AccordionItem title="Campos de documentos">
          <MaritalStatus className={"mb-3"} register={register} />
          {errors.marital_status && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.marital_status.message}</p>}
          <CommonLawMarriage className={"mb-3"} register={register} />
          {errors.common_law_marriage && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.common_law_marriage.message}</p>}
          <RG className={"mb-3"} register={register} setValue={setValue} />
          {errors.rg && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.rg.message}</p>}
          <UfForRG className={"mb-3"} register={register} />
          {errors.uf_for_RG_id && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.uf_for_RG_id.message}</p>}
          <IssuingBody className={"mb-3"} register={register} />
          {errors.issuing_body_id && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.issuing_body_id.message}</p>}
          <CPF className={"mb-3"} register={register} setValue={setValue} />
          {errors.cpf && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.cpf.message}</p>}
          <MotherName className={""} register={register} />
          {errors.mother_name && <p className="text-red-500 my-3 border-b-2 border-red-500">{errors.mother_name.message}</p>}
        </AccordionItem>
      </div>
      {
          ["previdenciario","civel","administrativo"].includes(watch().power_of_attorney) && (
          <div className="bg-gray-200 p-5 border-b-2 border-gray-400">
            <AccordionItem title="Campos extras">
              <CEP className={"mb-3"} register={register} setValue={setValue} />
              {errors.cep && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.cep.message}</p>}
              <StateForAddress className={"mb-3"} register={register} watch={watch} setValue={setValue} apiInfo={cepData.uf} />
              {errors.state_for_address && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.state_for_address.message}</p>}
              <City className={"mb-3"} register={register} watch={watch} setValue={setValue} apiInfo={cepData.localidade} />
              {errors.city && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.city.message}</p>}
              <Neighborhood className={"mb-3"} register={register} watch={watch} setValue={setValue} apiInfo={cepData.bairro} />
              {errors.neighborhood && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.neighborhood.message}</p>}
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
              {errors.address_name && <p className="text-red-500 mt-3 border-b-2 border-red-500">{errors.address_name.message}</p>}
              {["previdenciario","civel"].includes(watch().power_of_attorney) && (
                <>
                  <AddressComplement className={"mt-3"} register={register} watch={watch} setValue={setValue} />
                  {errors.address_complement_type && <p className="text-red-500 mt-3 border-b-2 border-red-500">{errors.address_complement_type.message}</p>}
                  {errors.address_complement_name && <p className="text-red-500 mt-3 border-b-2 border-red-500">{errors.address_complement_name.message}</p>}
                </>
              )}
            </AccordionItem>
          </div>
        )
      }
      {
          ["trabalhista"].includes(watch().power_of_attorney) && (
            <div className="bg-gray-200 p-5 border-b-2 border-gray-400">
                <AccordionItem title="Campos extras">
                  <CTPSn className={"mb-3"} register={register} setValue={setValue} />
                  {errors.ctps_n && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.ctps_n.message}</p>}
                  <CTPSserie className={"mb-3"} register={register} setValue={setValue} />
                  {errors.ctps_serie && <p className="text-red-500 mb-3 border-b-2 border-red-500">{errors.ctps_serie.message}</p>}
                  <UfForCTPS className={""} register={register} />
                  {errors.uf_for_ctps_id && <p className="text-red-500 mt-3 border-b-2 border-red-500">{errors.uf_for_ctps_id.message}</p>}
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
