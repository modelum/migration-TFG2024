const mongoose = require('mongoose');

const DetallesUsuarios = new mongoose.Schema({
	Usuario_D_id: {
		type: Number,
		required: true,
		unique: true
	}
})
const Autor = new mongoose.Schema({
	AutorID_id: {
		type: Number,
		required: true,
		unique: true
	}
})
const Libro = new mongoose.Schema({
	LibroID_id: {
		type: Number,
		required: true,
		unique: true
	},
	ReservaIDReservas: {
		type: Number,
		required: true,
		unique: false
	},
	Reservas: {
		type: [Number],
		ref: 'Reserva',
		required: true
	}
})
const Usuario = new mongoose.Schema({
	UsuarioID_id: {
		type: Number,
		required: true,
		unique: true
	},
	DetallesUsuarios: [DetallesUsuarios]
})
const Reserva = new mongoose.Schema({
	ReservaID_id: {
		type: Number,
		required: true,
		unique: true
	},
	Libro_R: {
		type: Number,
		required: true,
		unique: false
	},
	Usuario_R: {
		type: Number,
		required: true,
		unique: false
	},
	FechaReserva: {
		type: String,
		required: true,
		unique: false
	},
	Usuario_Usuario_FK: {
		type: Number,
		ref: 'Usuario',
		required: true
	}
})
const Libros_Autores = new mongoose.Schema({
	Editorial: {
		type: String,
		required: true,
		unique: false
	},
	Libros_Autores_id: {
		type: String,
		required: true,
		unique: true
	},
	Libro_FK: {
		type: Number,
		ref: 'Libro',
		required: true
	},
	Autor_FK: {
		type: Number,
		ref: 'Autor',
		required: true
	}
})
