package org.alvarowau.apicontact.models;

public class ContactDTO {

    private int id;
    private String tipoNotif;
    private String mensaje;
    private String telefono;
    private String fechaNacimiento;
    private String nombre;

    // Constructor vacío
    public ContactDTO() {
    }

    // Constructor completo
    public ContactDTO(int id, String tipoNotif, String mensaje, String telefono, String fechaNacimiento, String nombre) {
        this.id = id;
        this.tipoNotif = tipoNotif;
        this.mensaje = mensaje;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.nombre = nombre;
    }
    public ContactDTO(int id, String nombre, String telefono,String fechaNacimiento){
        this.id = id;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.nombre = nombre;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipoNotif() {
        return tipoNotif;
    }

    public void setTipoNotif(String tipoNotif) {
        this.tipoNotif = tipoNotif;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "ContactDTO{" +
                "id=" + id +
                ", tipoNotif='" + tipoNotif + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", telefono='" + telefono + '\'' +
                ", fechaNacimiento='" + fechaNacimiento + '\'' +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
