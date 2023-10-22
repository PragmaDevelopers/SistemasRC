import Link from "next/link"

export default function Home(){
    return (
        <>
            <Link className="text-blue-500" href={"./signup"}>Link para a área cadastro</Link>
            <Link className="text-blue-500" href={"./pdf_page"}>Link para a área de pdf</Link>
        </>
    )
}