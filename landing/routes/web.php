<?php

use App\Http\Controllers\LandingController;
use App\Http\Controllers\RelatoController;
use Illuminate\Support\Facades\Route;

Route::get('/', LandingController::class)->name('landing');

// Demonstração web do app (mesmo layout do protótipo Android).
Route::view('/app', 'app-demo')->name('app.demo');

Route::prefix('api')->group(function () {
    Route::get('/relatos', [RelatoController::class, 'index'])->name('api.relatos.index');
    Route::post('/relatos', [RelatoController::class, 'store'])->name('api.relatos.store');
});
