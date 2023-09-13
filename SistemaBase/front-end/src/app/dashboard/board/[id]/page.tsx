export default function Page({ params }: { params: { id: string } }) {
    return (
        <main>
            <h1>Test {params.id}</h1>
        </main>
    );
}
